var session = null;
var latency = null;
var stat = null;
var currentMenu;

function isLogin() {
	return session !== null;
}

function login() {
	$zkAddress = $("#zkAddress");
	$cluster = $("#cluster");

	if ($zkAddress.val() === "") {
		alert("Please insert zookeepr address");
		$zkAddress.focus();
		return;
	}
	if ($cluster.val() === "") {
		alert("Please insert cluster name");
		$cluster.focus();
		return;
	}
	session = {
		zkAddress: $zkAddress.val(),
		cluster: $cluster.val()
	};
	displayMenu();
}

function displayMenu() {
	$("#login").hide();
	$("#menu").show();
	$('#menu').find('a[href="#latency"]').tab('show').click()
}

function displayLatency() {
	currentMenu = "latency";
	if (latency === null) {
		latency = new callLatency();
	}
	if (latency.isOpen()) {
		setStop();
	} else {
		setStart();
	}
}

function displayStat() {
	currentMenu = "stat";
	if (stat === null) {
		stat = callStat();
	}
	if (stat.isOpen()) {
		setStop();
	} else {
		setStart();
	}
}

function timeToString(time) {
	return time.hour + ":"
		+ (time.minute < 10 ? "0" + time.minute : time.minute) + ":"
		+ (time.second < 10 ? "0" + time.second : time.second);
}

function scrollToBottom() {
	$("html, body").animate({scrollTop: $(document).height() - $(window).height()});
}

function start() {
	if (currentMenu === "latency") {
		if (!latency.isOpen()) {
			latency = callLatency();
		}
	} else if (currentMenu === "stat") {
		if (!stat.isOpen()) {
			stat = callStat();
		}
	} else {
		// Nothing
	}
	setStop();
}

function stop() {
	if (currentMenu === "latency") {
		if (latency.isOpen()) {
			latency.close();
		}
	} else if (currentMenu === "stat") {
		if (stat.isOpen()) {
			stat.close();
		}
	} else {
		// Nothing
	}
	setStart();
}

function setStop() {
	$("#btnStart").hide();
	$("#btnStop").show();
}

function setStart() {
	$("#btnStart").show();
	$("#btnStop").hide();
}

function callLatency() {
	var _socket = null;
	var _isOpen = false;

	console.log("Open Latency " + session.zkAddress + " " + session.cluster);
	_socket = new WebSocket("ws://" + window.location.host + "/logs?zkAddress=" + session.zkAddress + "&cluster=" + session.cluster);

	_socket.onerror = function () {
		console.log("Latency socket error");
	};

	_socket.onopen = function () {
		console.log("Latency Connected");
		_isOpen = true;
		setStop();
		_socket.send("/latencies")
	};

	_socket.onclose = function () {
		console.log("Latency disconnected");
		_isOpen = false;
		setStart();
		// setTimeout(callLatency, 5000);
	};

	_socket.onmessage = function (event) {
		received(event.data);
	};

	function isOpen() {
		return _isOpen;
	}

	function close() {
		_socket.close();
	}

	var rowCount = 1;

	function received(message) {
		var latency = JSON.parse(message);
		var $latencyRows = $("#latencyRows");
		$latencyRows.append(
			"<tr>" +
			"    <td class='text-center'>" + timeToString(latency.loggedAt.time) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under1ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under2ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under4ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under8ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under16ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under32ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under64ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under128ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under256ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under512ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.under1024ms) + "</td>\n" +
			"    <td class='text-right'>" + $.number(latency.over1024ms) + "</td>\n" +
			"</tr>");
		if (rowCount % 20 === 0) {
			$latencyRows.append(
				"<tr>\n" +
				"    <th class='text-center'>Datetime</th>\n" +
				"    <th class='text-right'>1ms</th>\n" +
				"    <th class='text-right'>2ms</th>\n" +
				"    <th class='text-right'>4ms</th>\n" +
				"    <th class='text-right'>8ms</th>\n" +
				"    <th class='text-right'>16ms</th>\n" +
				"    <th class='text-right'>32ms</th>\n" +
				"    <th class='text-right'>64ms</th>\n" +
				"    <th class='text-right'>128ms</th>\n" +
				"    <th class='text-right'>256ms</th>\n" +
				"    <th class='text-right'>512ms</th>\n" +
				"    <th class='text-right'>1024ms</th>\n" +
				"    <th class='text-right'>Over 1024ms</th>\n" +
				"</tr>"
			);
			rowCount = 0;
		}
		rowCount++;
		scrollToBottom();
	}

	return {
		close: close,
		isOpen: isOpen,
	}
}

function callStat() {
	var _socket = null;
	var _isOpen = false;

	console.log("Open Stat" + session.zkAddress + " " + session.cluster);
	_socket = new WebSocket("ws://" + window.location.host + "/logs?zkAddress=" + session.zkAddress + "&cluster=" + session.cluster);

	_socket.onerror = function () {
		console.log("Stat socket error");
	};

	_socket.onopen = function () {
		console.log("Stat Connected");
		_isOpen = true;
		setStop();
		_socket.send("/stats")
	};

	_socket.onclose = function () {
		console.log("Stat disconnected");
		_isOpen = false;
		setStart();
		// setTimeout(callStat, 5000);
	};

	_socket.onmessage = function (event) {
		received(event.data);
	};

	function isOpen() {
		return _isOpen;
	}

	function close() {
		_socket.close();
	}

	var rowCount = 1;

	function received(message) {
		var stat = JSON.parse(message);
		var $statRows = $("#statRows");
		$statRows.append(
			"<tr>" +
			"    <td class='text-center'>" + timeToString(stat.loggedAt.time) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.redis) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.pg) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.connection) + "</td>\n" +
			"    <td class='text-right'>" + toByteString(stat.mem) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.ops) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.hits) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.misses) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.keys) + "</td>\n" +
			"    <td class='text-right'>" + $.number(stat.expires) + "</td>\n" +
			"</tr>");
		if (rowCount % 20 === 0) {
			$statRows.append(
				"<tr>\n" +
				"    <th class='text-center'>Datetime</th>\n" +
				"    <th class='text-right'>redis</th>\n" +
				"    <th class='text-right'>PG</th>\n" +
				"    <th class='text-right'>connection</th>\n" +
				"    <th class='text-right'>mem</th>\n" +
				"    <th class='text-right'>OPS</th>\n" +
				"    <th class='text-right'>hits</th>\n" +
				"    <th class='text-right'>misses</th>\n" +
				"    <th class='text-right'>keys</th>\n" +
				"    <th class='text-right'>expires</th>\n" +
				"</tr>"
			);
			rowCount = 0;
		}
		rowCount++;
		scrollToBottom();
	}

	return {
		close: close,
		isOpen: isOpen
	}
}

function toByteString(byteValue) {
	return byteValue.value + " " + byteValue.unit;
}

