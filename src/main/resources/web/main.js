var session = null;
var latency = null;
var stat = null;

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
	if (latency !== null) {
		return;
	}
	latency = callLatency();
}

function displayStat() {
	if (stat !== null) {
		return;
	}
	stat = callStat();
}

function timeToString(time) {
	return time.hour + ":"
		+ (time.minute < 10 ? "0" + time.minute : time.minute) + ":"
		+ (time.second < 10 ? "0" + time.second : time.second);
}

function scrollToBottom() {
	$("html, body").animate({ scrollTop: $(document).height()-$(window).height() });
}

function callLatency() {
	var socket = null;

    console.log("Begin Latency " + session.zkAddress + " " + session.cluster);
    socket = new WebSocket("ws://" + window.location.host + "/logs?zkAddress=" + session.zkAddress + "&cluster=" + session.cluster);

    socket.onerror = function() {
        console.log("Latency socket error");
    };

    socket.onopen = function() {
        console.log("Latency Connected");
        socket.send("/latencies")
    };

    socket.onclose = function() {
        console.log("Latency disconnected");
        setTimeout(Latency, 5000);
    };

    socket.onmessage = function(event) {
        received(event.data);
    };

	function close() {
		socket.close()
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
}

function callStat() {
	var socket = null;

	console.log("Begin Stat" + session.zkAddress + " " + session.cluster);
	socket = new WebSocket("ws://" + window.location.host + "/logs?zkAddress=" + session.zkAddress + "&cluster=" + session.cluster);
	socket.onerror = function() {
		console.log("Stat socket error");
	};

	socket.onopen = function() {
		console.log("Stat Connected");
		socket.send("/stats")
	};

	socket.onclose = function() {
		console.log("Stat disconnected");
		setTimeout(Stat, 5000);
	};

	socket.onmessage = function(event) {
		received(event.data);
	};

	function close() {
	    socket.close()
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
}

function toByteString(byteValue) {
	return byteValue.value + " " + byteValue.unit;
}

