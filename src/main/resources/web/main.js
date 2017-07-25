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

function stopScrollToBottom() {
	$("html, body").stop().clearQueue();
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

	function toCssClass(number, className) {
		return number > 0 ? className : "";
	}

	function toNumberString(number) {
		if (number === 0) {
			return "";
		}
		return $.number(number);
	}

	var rowCount = 1;

	function received(message) {
		var latency = JSON.parse(message);
		var $latencyRows = $("#latencyRows");
		$latencyRows.append(
			"<tr>" +
			"    <td class='text-center'>" + timeToString(latency.loggedAt.time) + "</td>\n" +
			"    <td class='text-right " + toCssClass(latency.under1ms, "text-success") + "'><strong>" + toNumberString(latency.under1ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under2ms, "text-success") + "'><strong>" + toNumberString(latency.under2ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under4ms, "text-success") + "'><strong>" + toNumberString(latency.under4ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under8ms, "text-muted") + "'><strong>" + toNumberString(latency.under8ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under16ms, "text-muted") + "'><strong>" + toNumberString(latency.under16ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under32ms, "text-muted") + "'><strong>" + toNumberString(latency.under32ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under64ms, "text-warning") + "'><strong>" + toNumberString(latency.under64ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under128ms, "text-warning") + "'><strong>" + toNumberString(latency.under128ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under256ms, "text-warning") + "'><strong>" + toNumberString(latency.under256ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under512ms, "text-danger") + "'><strong>" + toNumberString(latency.under512ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.under1024ms, "text-danger") + "'><strong>" + toNumberString(latency.under1024ms) + "</strong></td>\n" +
			"    <td class='text-right " + toCssClass(latency.over1024ms, "text-danger") + "'><strong>" + toNumberString(latency.over1024ms) + "</strong></td>\n" +
			"</tr>"
		);

		if (rowCount % 20 === 0) {
			$latencyRows.append(
				"<tr>\n" +
				"    <th class='text-center'>Datetime</th>\n" +
				"    <th class='bg-success text-right text-white'>1ms</th>\n" +
				"    <th class='bg-success text-right text-white'>2ms</th>\n" +
				"    <th class='bg-success text-right text-white'>4ms</th>\n" +
				"    <th class='bg-faded text-right text-muted'>8ms</th>\n" +
				"    <th class='bg-faded text-right text-muted'>16ms</th>\n" +
				"    <th class='bg-faded text-right text-muted'>32ms</th>\n" +
				"    <th class='bg-warning text-right text-white'>64ms</th>\n" +
				"    <th class='bg-warning text-right text-white'>128ms</th>\n" +
				"    <th class='bg-warning text-right text-white'>256ms</th>\n" +
				"    <th class='bg-danger text-right text-white'>512ms</th>\n" +
				"    <th class='bg-danger text-right text-white'>1024ms</th>\n" +
				"    <th class='bg-danger text-right text-white'>Over 1024ms</th>\n" +
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

