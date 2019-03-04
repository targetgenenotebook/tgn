function setCookie(cname,cvalue) {
	document.cookie = cname + '=' + cvalue + ';path=/';
}

function getCookie(cname) {
	var name = cname + '=';
	var decodedCookie = decodeURIComponent(document.cookie);
	var ca = decodedCookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

/*
function AcceptAgreement() {
	setCookie('tgn_agree1', 'agree');
	$('.tgn_agree').hide();
}
*/

function UnreviewedCheckin() {
	var url = "http://"+window.location.host+"/unreviewedcheckin";
	var to_use = "";		
	var jqxhr = $.getJSON(url, function() {
	}).done(function(data) {
		if (data.isok=="ok") {
			var gobj = JSON.parse(data.gene_list);
			for (var i=0; i<gobj.length; ++i) {	
				var idchk = 'unrev_'+gobj[i].gene;
				var myele = document.getElementById(idchk);
				if (myele!=null) {	
					myele.innerHTML = gobj[i].unreviewed;
				}
			}
			$('#available_notebooks').trigger('update');
		} else {
			console.log('problem recovering unreviewed counts');
			alert("Problem recovering unreviewed counts");
		}
		setTimeout(UnreviewedCheckin, 300000);				
		return;	
	})
	.fail(function(xhr, desc, err) {
		alert("Problem recovering unreviewed counts");	
		setTimeout(UnreviewedCheckin, 300000);
		return;
	});
};

var master_busy = false;
function aktimeStamp() {
	var now = new Date();
	var date = [ now.getMonth() + 1, now.getDate(), now.getFullYear() ];
	var time = [ now.getHours(), now.getMinutes(), now.getSeconds() ];
	var suffix = ( time[0] < 12 ) ? "AM" : "PM";
	time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;
	time[0] = time[0] || 12;
	for ( var i = 1; i < 3; i++ ) {
		if ( time[i] < 10 ) {
			time[i] = "0" + time[i];
		}
	}
	return date.join("/") + " " + time.join(":") + " " + suffix;
}

var det_timerchecks = [];
var det_timercheckids = [];
function startdetailcommenttimer(textarea) {
	var db_id = textarea.getAttribute("data-dbid");
	var fromwhere = textarea.getAttribute("data-fromwhere");
	var index = -1;
	for (var i=0; i<det_timercheckids.length; ++i) {
		if (det_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(det_timerchecks[index]);
	}
	var comtext = textarea.value;

	if (index>-1) {
		det_timerchecks[index] = setTimeout(function() {updatedetailcomment(db_id, comtext, fromwhere)}, 2000);
	} else {
		det_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updatedetailcomment(db_id, comtext, fromwhere)}, 2000);
		det_timerchecks.push(tmp);
	}
};

var gc_timerchecks = [];
var gc_timercheckids = [];
function startgwascommenttimer(textarea) {
	var db_id = textarea.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<gc_timercheckids.length; ++i) {
		if (gc_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(gc_timerchecks[index]);
	}
	var comtext = textarea.value;
	if (index>-1) {
		gc_timerchecks[index] = setTimeout(function() {updategwascomment(db_id, comtext)}, 2000);
	} else {
		gc_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updategwascomment(db_id, comtext)}, 2000);
		gc_timerchecks.push(tmp);
	}
};

var gpc_timerchecks = [];
var gpc_timercheckids = [];
function startgenephencommenttimer(textarea) {
	var db_id = textarea.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<gpc_timercheckids.length; ++i) {
		if (gpc_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(gpc_timerchecks[index]);
	}
	var comtext = textarea.value;
	if (index>-1) {
		gpc_timerchecks[index] = setTimeout(function() {updategenephencomment(db_id, comtext)}, 2000);
	} else {
		gpc_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updategenephencomment(db_id, comtext)}, 2000);
		gpc_timerchecks.push(tmp);
	}
};

var ec_timerchecks = [];
var ec_timercheckids = [];
function starteqtlcommenttimer(textarea) {
	var db_id = textarea.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<ec_timercheckids.length; ++i) {
		if (ec_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(ec_timerchecks[index]);
	}
	var comtext = textarea.value;
	if (index>-1) {
		ec_timerchecks[index] = setTimeout(function() {updateeqtlcomment(db_id, comtext)}, 2000);
	} else {
		ec_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updateeqtlcomment(db_id, comtext)}, 2000);
		ec_timerchecks.push(tmp);
	}
};

var sdc_timerchecks = [];
var sdc_timercheckids = [];
function startsourcecommenttimer(textarea) {
	var db_id = textarea.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<sdc_timercheckids.length; ++i) {
		if (sdc_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(sdc_timerchecks[index]);
	}
	var comtext = textarea.value;
	if (index>-1) {
		sdc_timerchecks[index] = setTimeout(function() {updatesourcecomment(db_id, comtext)}, 2000);
	} else {
		sdc_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updatesourcecomment(db_id, comtext)}, 2000);
		sdc_timerchecks.push(tmp);
	}
};

function startupdatesummarytimer() {
	if (updatesummarycheck!=null) {
		clearTimeout(updatesummarycheck);
	}
	updatesummarycheck = setTimeout(function() {UpdateSummary()}, 2000);
};

var gsdn_timerchecks = [];
var gsdn_timercheckids = [];
function startgwassvgdisplaynametimer(inputelement) {
	var db_id = inputelement.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<gsdn_timercheckids.length; ++i) {
		if (gsdn_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(gsdn_timerchecks[index]);
	}
	var sdntext = inputelement.value;
	if (index>-1) {
		gsdn_timerchecks[index] = setTimeout(function() {updategwassvgdisplayname(db_id, sdntext)}, 2000);
	} else {
		gsdn_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updategwassvgdisplayname(db_id, sdntext)}, 2000);
		gsdn_timerchecks.push(tmp);
	}
};

var esdn_timerchecks = [];
var esdn_timercheckids = [];
function starteqtlsvgdisplaynametimer(inputelement) {
	var db_id = inputelement.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<esdn_timercheckids.length; ++i) {
		if (esdn_timercheckids[i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(esdn_timerchecks[index]);
	}
	var sdntext = inputelement.value;
	if (index>-1) {
		esdn_timerchecks[index] = setTimeout(function() {updateeqtlsvgdisplayname(db_id, sdntext)}, 2000);
	} else {
		esdn_timercheckids.push(db_id);
		var tmp =  setTimeout(function() {updateeqtlsvgdisplayname(db_id, sdntext)}, 2000);
		esdn_timerchecks.push(tmp);
	}
};

var updatesummarycheck = null;
UpdateSummary = function() {
	if (master_busy) {
		updatesummarycheck = setTimeout(function() {UpdateSummary()}, 2000);
		return;
	}
	master_busy = true;
	updatesummarycheck = null;
	var c_val = $( '#aksummaryarea' ).attr('data-curval');
	var n_val = $( '#aksummaryarea' ).val();
	var whattodo = {
			todo: "updatesummary",
			summary: n_val
	};
	$.ajax({
		url: "http://"+window.location.host+"/updatesummary/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				$('#aksummaryarea').val(c_val);
				console.log('updatesummary not successful at '+stamp);
				master_busy = false;
				alert("Problem saving summary to db");
			} else {
				$('#aksummaryarea').attr('data-curval', n_val);
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			console.log('UpdateSummary not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('#aksummaryarea').val(c_val);
			master_busy = false;
			alert("Problem saving summary to db");
		}
	});
};

updategwascomment = function(dbid, commenttext) {
	var index = -1;
	for (var i=0; i<gc_timercheckids.length; ++i) {
		if (gc_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			gc_timerchecks[index] = setTimeout(function() {updategwascomment(dbid, commenttext)}, 2000);
		} else {
			gc_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updategwascomment(dbid, commenttext)}, 2000);
			gc_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		gc_timerchecks.splice(index,1);
		gc_timercheckids.splice(index,1);
	}
	var el_chk = document.getElementById("gwasrow"+dbid);
	if (el_chk==null) { // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = $("#gwasrow"+dbid+" .ak_gwas_comment").attr('data-curval');
	var whattodo = {
			todo: "updategwascomment",
			gwas_comment: commenttext,
			gwas_db_id: dbid
	};
	$.ajax({
		url: "http://"+window.location.host+"/updategwascomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				var ele_chk = document.getElementById("gwasrow"+dbid);
				if (ele_chk!=null) { // make sure row didn't get deleted somehow

					$("#gwasrow"+dbid+" .ak_gwas_comment").val(c_val);
				}
				console.log('updategwascomment not successful at '+stamp);
				master_busy = false;
				alert("Problem saving comment to db");
			} else {
				var ele_chk = document.getElementById("gwasrow"+dbid);
				if (ele_chk!=null) {  // update the row, if it still there (no reason it shouldn't be)
					$("#gwasrow"+dbid+" .ak_gwas_comment").attr('data-curval', commenttext);
				}
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("gwasrow"+dbid);
			if (ele_chk!=null) {
				$("#gwasrow"+dbid+" .ak_gwas_comment").val(c_val);
			}
			console.log('updategwascomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving comment to db");
		}
	});
};

updategenephencomment = function(dbid, commenttext) {
	var index = -1;
	for (var i=0; i<gpc_timercheckids.length; ++i) {
		if (gpc_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			gpc_timerchecks[index] = setTimeout(function() {updategenephencomment(dbid, commenttext)}, 2000);
		} else {
			gpc_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updategenephencomment(dbid, commenttext)}, 2000);
			gpc_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		gpc_timerchecks.splice(index,1);
		gpc_timercheckids.splice(index,1);
	}
	var el_chk = document.getElementById("genephenrow"+dbid);
	if (el_chk==null) { // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = $("#genephenrow"+dbid+" .ak_genephen_comment").attr('data-curval');
	var whattodo = {
			todo: "updategenephencomment",
			genephen_comment: commenttext,
			genephen_db_id: dbid
	};
	$.ajax({
		url: "http://"+window.location.host+"/updategenephencomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				var ele_chk = document.getElementById("gwasrow"+dbid);
				if (ele_chk!=null) { // make sure row didn't get deleted somehow

					$("#genephenrow"+dbid+" .ak_genephen_comment").val(c_val);
				}
				console.log('updategenephencomment not successful at '+stamp);
				master_busy = false;
				alert("Problem saving comment to db");
			} else {
				var ele_chk = document.getElementById("genephenrow"+dbid);
				if (ele_chk!=null) {  // update the row, if it still there (no reason it shouldn't be)
					$("#genephenrow"+dbid+" .ak_genephen_comment").attr('data-curval', commenttext);
				}
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("genephenrow"+dbid);
			if (ele_chk!=null) {
				$("#genephenrow"+dbid+" .ak_genephen_comment").val(c_val);
			}
			console.log('updategenephencomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving comment to db");
		}
	});
};

updateeqtlcomment = function(dbid, commenttext) {
	var index = -1;
	for (var i=0; i<ec_timercheckids.length; ++i) {
		if (ec_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			ec_timerchecks[index] = setTimeout(function() {updateeqtlcomment(dbid, commenttext)}, 2000);
		} else {
			ec_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updateeqtlcomment(dbid, commenttext)}, 2000);
			ec_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		ec_timerchecks.splice(index,1);
		ec_timercheckids.splice(index,1);
	}
	var el_chk = document.getElementById("eqtlrow"+dbid);
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it

		master_busy = false;
		return;
	}
	var c_val = $("#eqtlrow"+dbid+" .ak_eqtl_comment").attr('data-curval');
	var whattodo = {
			todo: "updateeqtlcomment",
			eqtl_comment: commenttext,
			eqtl_db_id: dbid
	};
	$.ajax({
		url: "http://"+window.location.host+"/updateeqtlcomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				var ele_chk = document.getElementById("eqtlrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow

					$("#eqtlrow"+dbid+" .ak_eqtl_comment").val(c_val);
				}
				console.log('updateeqtlcomment not successful at '+stamp);
				master_busy = false;
				alert("Problem saving comment to db");
			} else {
				var ele_chk = document.getElementById("eqtlrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow

					$("#eqtlrow"+dbid+" .ak_eqtl_comment").attr('data-curval', commenttext);
				}
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("eqtlrow"+dbid);
			if (ele_chk!=null) {
				$("#eqtlrow"+dbid+" .ak_eqtl_comment").val(c_val);
			}
			console.log('updateeqtlcomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving comment to db");
		}
	});
};

updatesourcecomment = function(dbid, commenttext) {
	var index = -1;
	for (var i=0; i<sdc_timercheckids.length; ++i) {
		if (sdc_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			sdc_timerchecks[index] = setTimeout(function() {updatesourcecomment(dbid, commenttext)}, 2000);
		} else {
			sdc_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updatesourcecomment(dbid, commenttext)}, 2000);
			sdc_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		sdc_timerchecks.splice(index,1);
		sdc_timercheckids.splice(index,1);
	}
	var sd_chk = document.getElementById("sourcedocumentsrow"+dbid);
	if (sd_chk==null) { // row got deleted somehow, weird, but we won't error because of it

		master_busy = false;
		return;
	}
	var c_val = $("#sourcedocumentsrow"+dbid+" .ak_source_comment").attr('data-curval');
	var whattodo = {
			todo: "updatesourcecomment",
			source_comment: commenttext,
			source_db_id: dbid
	};
	$.ajax({
		url: "http://"+window.location.host+"/updatesourcecomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				var sdo_chk = document.getElementById("sourcedocumentsrow"+dbid);
				if (sdo_chk!=null) {  // row got deleted somehow

					$("#sourcedocumentsrow"+dbid+" .ak_source_comment").val(c_val);
				}
				console.log('updatesourcecomment not successful at '+stamp);
				master_busy = false;
				alert("Problem saving comment to db");
			} else {
				var sdo_chk = document.getElementById("sourcedocumentsrow"+dbid);
				if (sdo_chk!=null) {  // row got deleted somehow

					$("#sourcedocumentsrow"+dbid+" .ak_source_comment").attr('data-curval', commenttext);
				}
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var sdo_chk = document.getElementById("sourcedocumentsrow"+dbid);
			if (sdo_chk!=null) {
				$("#sourcedocumentsrow"+dbid+" .ak_source_comment").val(c_val);
			}
			console.log('updatesourcecomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving comment to db");
		}
	});
};

updatedetailcomment = function(dbid, detailcomment, fromwhere) {
	var index = -1;
	for (var i=0; i<det_timercheckids.length; ++i) {
		if (det_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			det_timerchecks[index] = setTimeout(function() {updatedetailcomment(dbid, detailcomment, fromwhere)}, 2000);
		} else {
			det_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updatedetailcomment(dbid, detailcomment, fromwhere)}, 2000);
			det_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		det_timerchecks.splice(index,1);
		det_timercheckids.splice(index,1);
	}
	var el_chk;
	if (fromwhere==1) {
		el_chk = document.getElementById("detailrow"+dbid);
	} else {
		el_chk = document.getElementById("detailrowsection"+dbid);
	}
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = el_chk.getAttribute("data-curval");
	if (detailcomment=='') {
		$("#detailrow"+dbid).val(c_val);
		if ($("#detailrowsection"+dbid).length>0) {
			$("#detailrowsection"+dbid).val(c_val);
		}
		master_busy = false;
		return;
	}
	var whattodo = {
			todo: "updatedetailcomment",
			comment: detailcomment,
			detail_db_id: dbid
	};
	$.ajax({
		url: "http://"+window.location.host+"/updatedetailcomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				var ele_chk = document.getElementById("detailrow"+dbid);
				var ele_exists = 0;
				if (ele_chk!=null) {
					ele_exists=1;
				}
				if (ele_exists==1) {$("#detailrow"+dbid).attr('data-curval', detailcomment);}
				if ($("#detailrowsection"+dbid).length>0) {
					$("#detailrowsection"+dbid).attr('data-curval', detailcomment);
				}
				if (fromwhere==1) {
					if ($("#detailrowsection"+dbid).length>0) {
						$("#detailrowsection"+dbid).val(detailcomment);
					}
				} else {
					if (ele_exists==1) {$("#detailrow"+dbid).val(detailcomment);}
				}
				master_busy = false;
			} else {
				var ele_chk = document.getElementById("detailrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow
					$("#detailrow"+dbid).val(c_val);
				}
				if ($("#detailrowsection"+dbid).length>0) {
					$("#detailrowsection"+dbid).val(c_val);
				}
				console.log('updatedetailcomment not successful at '+stamp);
				master_busy = false;
				alert("Problem saving description to db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("detailrow"+dbid);
			if (ele_chk!=null) {  // row got deleted somehow
				$("#detailrow"+dbid).val(c_val);
			}
			if ($("#detailrowsection"+dbid).length>0) {
				$("#detailrowsection"+dbid).val(c_val);
			}
			console.log('updatedetailcomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving description to db");
		}
	});
};

updategwassvgdisplayname = function(dbid, svgdisplayname) {
	var index = -1;
	for (var i=0; i<gsdn_timercheckids.length; ++i) {
		if (gsdn_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			gsdn_timerchecks[index] = setTimeout(function() {updategwassvgdisplayname(dbid, svgdisplayname)}, 2000);
		} else {
			gsdn_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updategwassvgdisplayname(dbid, svgdisplayname)}, 2000);
			gsdn_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		gsdn_timerchecks.splice(index,1);
		gsdn_timercheckids.splice(index,1);
	}
	var el_chk = document.getElementById("gwasrow"+dbid);
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = $("#gwasrow"+dbid+" .ak_gwas_sdn").attr('data-curval');
	if (svgdisplayname=='') {
		$("#gwasrow"+dbid+" .ak_gwas_sdn").val(c_val);
		master_busy = false;
		return;
	}
	var whattodo = {
			todo: "updategwassvgdisplayname",
			svgdisplayname: svgdisplayname,
			gwas_db_id: dbid,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updategwassvgdisplayname/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				var ele_chk = document.getElementById("gwasrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow
					$("#gwasrow"+dbid+" .ak_gwas_sdn").attr('data-curval', svgdisplayname);
				}
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svggwas_'+dbid) {
						var supl = document.getElementById('svggwas_'+dbid).getAttribute("data-supl");
						fill_supl_div(supl, 'svggwas_'+dbid);
					}
				}
				master_busy = false;
			} else {
				var ele_chk = document.getElementById("gwasrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow
					$("#gwasrow"+dbid+" .ak_gwas_sdn").val(c_val);
				}
				console.log('updategwassvgdisplayname not successful at '+stamp);
				master_busy = false;
				alert("Problem saving display name to db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("gwasrow"+dbid);
			if (ele_chk!=null) {  // row got deleted somehow
				$("#gwasrow"+dbid+" .ak_gwas_sdn").val(c_val);
			}
			console.log('updategwassvgdisplayname not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving display name to db");
		}
	});
};

updateeqtlsvgdisplayname = function(dbid, svgdisplayname) {
	var index = -1;
	for (var i=0; i<esdn_timercheckids.length; ++i) {
		if (esdn_timercheckids[i]==dbid) {
			index = i;
			break;
		}
	}
	if (master_busy) {
		if (index>-1) {
			esdn_timerchecks[index] = setTimeout(function() {updateeqtlsvgdisplayname(dbid, svgdisplayname)}, 2000);
		} else {
			esdn_timercheckids.push(dbid);
			var tmp = setTimeout(function() {updateeqtlsvgdisplayname(dbid, svgdisplayname)}, 2000);
			esdn_timerchecks.push(tmp);
		}
		return;
	}
	master_busy = true;
	if (index>-1) {
		esdn_timerchecks.splice(index,1);
		esdn_timercheckids.splice(index,1);
	}
	var el_chk = document.getElementById("eqtlrow"+dbid);
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = $("#eqtlrow"+dbid+" .ak_eqtl_sdn").attr('data-curval');
	if (svgdisplayname=='') {
		$("#eqtlrow"+dbid+" .ak_eqtl_sdn").val(c_val);
		master_busy = false;
		return;
	}
	var whattodo = {
			todo: "updateeqtlsvgdisplayname",
			svgdisplayname: svgdisplayname,
			eqtl_db_id: dbid,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updateeqtlsvgdisplayname/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				var ele_chk = document.getElementById("eqtlrow"+dbid);
				if (ele_chk!=null) { // row got deleted somehow

					$("#eqtlrow"+dbid+" .ak_eqtl_sdn").attr('data-curval', svgdisplayname);
				}
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svgeqtl_'+dbid) {
						var supl = document.getElementById('svgeqtl_'+dbid).getAttribute("data-supl");
						fill_supl_div(supl, 'svgeqtl_'+dbid);
					}
				}
				master_busy = false;
			} else {
				var ele_chk = document.getElementById("eqtlrow"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow

					$("#eqtlrow"+dbid+" .ak_eqtl_sdn").val(c_val);
				}
				console.log('updateeqtlsvgdisplayname not successful at '+stamp);
				master_busy = false;
				alert("Problem saving display name to db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById("eqtlrow"+dbid);
			if (ele_chk!=null) {  // row got deleted somehow

				$("#eqtlrow"+dbid+" .ak_eqtl_sdn").val(c_val);
			}
			console.log('updateeqtlsvgdisplayname not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving display name to db");
		}
	});
};

function AddEQTLOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var url = "http://"+window.location.host+"/get_pub_and_gene_list/"+encodeURIComponent(gene_symbol);
	var to_use = "";
	var jqxhr = $.getJSON(url, function() {
	}).done(function(data) {
		var stamp = aktimeStamp();
		if (data.isok=="ok") {
			var pobj = JSON.parse(data.pub_list);
			var gobj = JSON.parse(data.gene_list);
			var mydiv = document.getElementById("addeqtloverlay");
			mydiv.innerHTML = '';
			var dcontent = '<div id="akneweqtldiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
			dcontent += '<p style="color:#000; font-size: 20px;"></p>';
			dcontent += '<p style="color:#000; font-size: 20px;">New eQTL</p>';
			dcontent += '<p style="color:#000;">Source:';
			var s_html = '<select style="width:400px" class="ak_eqtl_overlay ak_eqtl_source_selector" >';
			s_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
			for (var i=0; i<pobj.length; ++i) {
				s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].column_display_text)+'</option>';
			}
			s_html += '</select>';
			dcontent += s_html+'</p>';
			dcontent += '<p style="color:#000;">Tissue: <input class="ak_eqtl_overlay" id="new_eqtl_tissue" style="text-align:center;width:250px;color:#000;" placeholder="Eg. Spleen" type="text" maxlength="150"></p>';
			dcontent += '<p style="color:#000;">Gene:';
			var g_html = '<select style="width:300px" class="ak_eqtl_overlay ak_eqtl_gene_selector" >';
			g_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
			for (var i=0; i<gobj.length; ++i) {
				g_html += '<option value="'+escapeHtml(gobj[i].gene_symbol)+'">'+escapeHtml(gobj[i].gene_symbol)+'</option>';
			}
			g_html += '</select>';
			dcontent += g_html+'</p>';
			dcontent += '<p style="color:#000;">Index variant: <input class="ak_eqtl_overlay" id="new_eqtl_indexvariant" style="text-align:center;width:105px;color:#000;" placeholder="Eg. rs123" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">Pvalue: <input class="ak_eqtl_overlay" id="new_eqtl_pvalue" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 1.0e-12" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">Beta: <input class="ak_eqtl_overlay" id="new_eqtl_beta" style="text-align:center;width:105px;color:#000;" placeholder="Eg. -0.808" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">Effect allele (optional): <input class="ak_eqtl_overlay" id="new_eqtl_allele" style="text-align:center;width:105px;color:#000;" placeholder="Eg. T" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000; vertical-align: middle;">Comment: <textarea id="new_eqtl_comment" rows="4" cols="50" style="color:#000; vertical-align: middle;" placeholder="Enter comment" class="ak_eqtl_overlay ak_tablecell" type="text"></textarea></p>';
			dcontent += '<p></p>';
			dcontent += '<button id="ak_add_eqtl_button" type="button" class="ak_eqtl_overlay btn btn-primary btn-sm" onclick="SaveNewEQTL()">Save</button>';
			dcontent += '<p style="color:#000; font-size: 20px;"></p>';
			dcontent += "</div>";
			mydiv.innerHTML = dcontent;
			var tmpb = document.createElement('button');
			tmpb.setAttribute('id', 'ak_add_eqtl_x_button');
			tmpb.setAttribute('onclick', 'RemoveAddEQTLOverlay()');
			tmpb.setAttribute('class', 'ak_eqtl_overlay btn btn-primary btn-sm');
			tmpb.style.verticalAlign='top';
			tmpb.style.display='inline-block';
			tmpb.innerHTML = 'X';
			mydiv.appendChild(tmpb);
			$(".ak_eqtl_source_selector").select2();
			$(".ak_eqtl_gene_selector").select2();
			mydiv.style.display = 'block';
			master_busy = false;
		} else {
			console.log('problem recovering references and genes at '+stamp);
			master_busy = false;
			alert("Problem recovering references and genes");
		}
		return;
	})
	.fail(function(xhr, desc, err) {
		master_busy = false;
		alert("Problem recovering references and genes");
		return;
	});
};

RemoveAddEQTLOverlay = function() {
	var mydiv = document.getElementById("addeqtloverlay");
	mydiv.style.display = 'none';
}

SaveNewEQTL = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_eqtl_overlay').attr('disabled', true);
	var new_tissue = document.getElementById("new_eqtl_tissue").value;
	var new_indexvariant = document.getElementById("new_eqtl_indexvariant").value;
	var new_pvalue = document.getElementById("new_eqtl_pvalue").value;
	var new_beta = document.getElementById("new_eqtl_beta").value;
	var new_allele = document.getElementById("new_eqtl_allele").value;
	var source_selector = document.getElementsByClassName("ak_eqtl_source_selector")[0];
	var source_id = -1;
	if (source_selector.selectedIndex>-1) {
		source_id = source_selector.options[source_selector.selectedIndex].value;
	}
	var gene_selector = document.getElementsByClassName("ak_eqtl_gene_selector")[0];
	var gene = "";
	if (gene_selector.selectedIndex>-1) {
		gene = gene_selector.options[gene_selector.selectedIndex].value;
	}
	var comment_text = document.getElementById("new_eqtl_comment").value;
	var whattodo = {
			todo: "createneweqtl",
			db_source_id: source_id,
			gene_symbol: gene,
			tissue: new_tissue,
			indexvariant: new_indexvariant,
			pvalue: new_pvalue,
			beta: new_beta,
			allele: new_allele,
			comment: comment_text
	};
	$.ajax({
		url: "http://"+window.location.host+"/createneweqtl/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('createnewcredibleset not successful at '+stamp);
				$('.ak_eqtl_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving new eQTL to db");
			} else {
				if (data.errors!="") {
					console.log('createneweqtl had user errors at '+stamp);
					$('.ak_eqtl_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$("#eqtltable").trigger('addRows', [data.newrow]);
					$("#eqtlrow"+data.eqtldbid+" .ak_eqtl_comment").attr('data-curval', $("#eqtlrow"+data.eqtldbid+" .ak_eqtl_comment").val());
					$("#eqtlrow"+data.eqtldbid+" .ak_eqtl_sdn").attr('data-curval', $("#eqtlrow"+data.eqtldbid+" .ak_eqtl_sdn").val());
					$('.ak_eqtl_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addeqtloverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('createneweqtl not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_eqtl_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving new eQTL to db");
		}
	});
}

UpdateEQTLShowHide = function(cbox) {
	var db_id = cbox.getAttribute("data-dbid");
	var s_or_h = "Hide";
	if (cbox.checked) { s_or_h = "Show";}
	if (master_busy) {
		if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "updateeqtlshowhide",
			show_or_hide: s_or_h,
			eqtl_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updateeqtlshowhide/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svgeqtl_'+db_id) {
						var svge = document.getElementById('svgeqtl_'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							fill_supl_div(supl, 'svgeqtl_'+db_id);
						} else {
							clear_supl_div();
						}
					}
					if (shown_supl_id.indexOf('ER'+db_id+'-')>-1) {
						var svge = document.getElementById('ER'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id=='ER'+db_id+'-') {clear_supl_div();}
							else {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var supl = svge.getAttribute("data-supl");
										var classname = svge.getAttribute("data-class");
										var ldgids = svge.getAttribute("data-ldgids");
										fill_supl_div(supl, ldgids, '3', classname);
										break;
									}
								}
							}
						}
					} else {
						var updateit = 0;
						var svge = document.getElementById('ER'+db_id);
						if (svge!=null) {
							var classname = svge.getAttribute("data-class");
							$("."+classname).each(function(i, obj) {
								var oid = obj.getAttribute('id');
								if (shown_supl_id.indexOf(oid+'-')>-1) {
									updateit=1;
									return false;
								}
							});
						}
						if (updateit==1) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('updateeqtlshowhide not successful at '+stamp);
				if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
				master_busy = false;
				alert("Problem showing/hiding eQTL result");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('updateeqtlshowhide not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
			master_busy = false;
			alert("Problem showing/hiding eQTL result");
		}
	});
};

UpdateSourceReviewed = function(cbox) {
	var db_id = cbox.getAttribute("data-dbid");
	var r_or_n = "Not Reviewed";
	if (cbox.checked) { r_or_n = "Reviewed";}
	if (master_busy) {
		if (r_or_n=='Not Reviewed') {cbox.checked = true;} else {cbox.checked = false;}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "updatesourcereview",
			reviewed_or_not: r_or_n,
			source_db_id: db_id
	};
	$.ajax({
		url: "http://"+window.location.host+"/updatesourcereview/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				if (r_or_n=='Not Reviewed') {cbox.checked = true;} else {cbox.checked = false;}
				console.log('updatesourcereview not successful at '+stamp);
				master_busy = false;
				alert("Problem saving review update to db");
			} else {
				master_busy = false;
				var rh3 = document.getElementById("referencesh3");
				rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			if (r_or_n=='Not Reviewed') {cbox.checked = true;} else {cbox.checked = false;}
			console.log('updatesourcereview not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving review update to db");
		}
	});
};

RemoveEQTLRow = function(whichdbid) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	whichdbid = whichdbid.replace("eqtlrow", "");
	$('.akeqtlrowclass'+whichdbid).attr('disabled', true);
	var whattodo = {
			todo: "removeeqtlrow",
			eqtl_db_id: whichdbid,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/removeeqtlrow/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				$("#eqtltable").trigger('disablePager');
				$("#eqtlrow"+whichdbid).remove();
				$("#eqtltable").trigger('enablePager');
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svgeqtl_'+whichdbid) {
						clear_supl_div();
					}
					if (shown_supl_id.indexOf('ER'+whichdbid+'-')>-1) {
						if (shown_supl_id=='ER'+whichdbid+'-') {clear_supl_div();}
						else {
							var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
							var sa = trimmed.split("-");
							var ix;
							for (ix=0; ix < sa.length; ++ix) {
								var svge = document.getElementById(sa[ix]);
								if (svge!=null) {
									var supl = svge.getAttribute("data-supl");
									var classname = svge.getAttribute("data-class");
									var ldgids = svge.getAttribute("data-ldgids");
									fill_supl_div(supl, ldgids, '3', classname);
									break;
								}
							}
						}
					} else {
						if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
							var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
							var sa = trimmed.split("-");
							var ix;
							for (ix=0; ix < sa.length; ++ix) {
								var svge = document.getElementById(sa[ix]);
								if (svge!=null) {
									var classname = svge.getAttribute("data-class");
									$("."+classname).each(function(i, obj) {
										obj.children[0].setAttribute('stroke-width', '3');
									});
									break;
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				console.log('removeeqtlrow not successful at '+stamp);
				$('.akeqtlrowclass'+whichdbid).attr('disabled', false);
				master_busy = false;
				alert("Problem removing eQTL entry from db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('removeeqtlrow not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.akeqtlrowclass'+whichdbid).attr('disabled', false);
			master_busy = false;
			alert("Problem removing eQTL entry from db");
		}
	});
};

CreateCredibleSetOverlay = function(selector) {
	var db_id = selector.getAttribute("data-dbid");
	var tr = document.getElementById("gwasrow"+db_id);
	var rsn = tr.cells[4].getElementsByTagName("A")[0].text;
	var mydiv = document.getElementById("crediblesetoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewcrediblesetdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">Custom Credible Set</p>';

	dcontent += '<p style="color:#000;">Name of credible set: <input class="ak_credibleset_overlay" id="new_credibleset_name" style="text-align:center;width:105px;color:#000;" placeholder="Eg. Some name" type="text" maxlength="15"></p>';
	dcontent += '<p style="color:#000;">Index-variant ('+rsn+') posterior: <input class="ak_credibleset_overlay" id="new_credibleset_indexvariant_posterior" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 0.75" type="text" maxlength="15"></p>';
	dcontent += '<p style="color:#000; margin-bottom:0px;">Other credible-set members:</p>';
	dcontent += '<table id="akcrediblesettable" style="width:300px"><thead><tr><th style="text-align: center;" class="resizable-false">Marker</th><th style="text-align: center;" class="resizable-false">Posterior</th></tr></thead>';
	dcontent += '<tbody>';
	for (var i=0; i<10; ++i) {
		dcontent += '<tr><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. rs123" type="text" maxlength="15"></td><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. 0.123" type="text" maxlength="15"></td></tr>';
	}
	dcontent += '</tbody>';
	dcontent += '</table>';
	dcontent += '<button type="button" class="ak_credibleset_overlay ak_table_button" onclick="AddCredibleSetOverlayTableRow()">Add Row</button>';
	dcontent += '<p></p>';
	dcontent += '<button id="ak_create_credibleset_button" type="button" class="ak_credibleset_overlay btn btn-primary btn-sm" onclick="SaveNewCredibleSet('+db_id+')">Create Credible Set</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_credibleset_x_button');
	tmpb.setAttribute('onclick', 'RemoveCredibleSetOverlay()');
	tmpb.setAttribute('class', 'ak_credibleset_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	$('#akcrediblesettable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['150px','150px']}});
	mydiv.style.display = 'block';
};

AddCredibleSetOverlayTableRow = function(selector) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var row = '<tr><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. rs123" type="text" maxlength="15"></td><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. 0.123" type="text" maxlength="15"></td></tr>';
	$("#akcrediblesettable").trigger('addRows', [row]);
	master_busy = false;
}

RemoveCredibleSetOverlay = function() {
	var mydiv = document.getElementById("crediblesetoverlay");
	mydiv.style.display = 'none';
}

SaveNewCredibleSet = function(db_id) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_credibleset_overlay').attr('disabled', true);
	var cs_name = document.getElementById("new_credibleset_name").value;
	var cs_index_posterior = document.getElementById("new_credibleset_indexvariant_posterior").value;
	var cs_table = document.getElementById("akcrediblesettable");
	var cs_rs_list = [];
	var cs_posteriors = [];
	for (var r=1; r<cs_table.rows.length; ++r) {
		var rs_name = cs_table.rows[r].cells[0].getElementsByTagName("input")[0].value;
		var rs_posterior = cs_table.rows[r].cells[1].getElementsByTagName("input")[0].value;
		if (rs_name || rs_posterior ) {
			cs_rs_list.push(rs_name);
			cs_posteriors.push(rs_posterior);
		}
	}
	var whattodo = {
			todo: "createnewcredibleset",
			gwas_db_id: db_id,
			credible_set_name: cs_name,
			index_variant_posterior: cs_index_posterior,
			member_rs_numbers: cs_rs_list,
			member_posteriors: cs_posteriors
	};
	$.ajax({
		url: "http://"+window.location.host+"/createnewcredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('createnewcredibleset not successful at '+stamp);
				$('.ak_credibleset_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving credible set to db");
			} else {
				if (data.errors!="") {
					console.log('createnewcredibleset had user errors at '+stamp);
					$('.ak_credibleset_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$('#gwasrow'+db_id+' .ak_mes_selector option[value="Add credible set"]').detach();
					var newOption1 = new Option(data.csname, 'Credible set', false, false);
					var newOption2 = new Option('Delete credible set', 'Delete credible set', false, false);
					updatemesresetting = 1;
					$('#gwasrow'+db_id+' .ak_mes_selector').append(newOption1).trigger('change');
					$('#gwasrow'+db_id+' .ak_mes_selector').append(newOption2).trigger('change');
					updatemesresetting = 0;
					$('.ak_credibleset_overlay').attr('disabled', false);
					var mydiv = document.getElementById("crediblesetoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('createnewcredibleset not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_credibleset_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving credible set to db");
		}
	});
}

DeleteCredibleSet = function(selector) {
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-cmes");
	var whattodo = {
			todo: "deletecredibleset",
			gwas_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/deletecredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				var eqtllist = JSON.parse(data.eqtl_ids);
				var overlap_counts = JSON.parse(data.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svggwas_'+db_id) {
						var svge = document.getElementById('svggwas_'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							fill_supl_div(supl, 'svggwas_'+db_id);
						} else {
							clear_supl_div();
						}
					}
					if (shown_supl_id.indexOf('GR'+db_id+'-')>-1) {
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id=='GR'+db_id+'-') {clear_supl_div();}
							else {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var supl = svge.getAttribute("data-supl");
										var classname = svge.getAttribute("data-class");
										var ldgids = svge.getAttribute("data-ldgids");
										fill_supl_div(supl, ldgids, '3', classname);
										break;
									}
								}
							}
						}
					} else {
						var updateit = 0;
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var classname = svge.getAttribute("data-class");
							$("."+classname).each(function(i, obj) {
								var oid = obj.getAttribute('id');
								if (shown_supl_id.indexOf(oid+'-')>-1) {
									updateit=1;
									return false;
								}
							});
						}
						if (updateit==1) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
				}
				$('#gwasrow'+db_id+' .ak_mes_selector option[value="Delete credible set"]').detach();
				$('#gwasrow'+db_id+' .ak_mes_selector option[value="Credible set"]').detach();
				var newOption1 = new Option('Add credible set', 'Add credible set', false, false);
				$('#gwasrow'+db_id+' .ak_mes_selector').append(newOption1).trigger('change');
				if (c_val=='Credible set') {
					selector.setAttribute("data-cmes", 'Unset');
					$(selector).val('Unset').trigger('change');
				}
				updatemesresetting=0;
				master_busy = false;
			} else {
				var stamp = aktimeStamp();
				console.log('deletecredibleset not successful at '+stamp);
				updatemesresetting=0;
				master_busy = false;
				alert("Problem deleting credible set from db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('deletecredibleset not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			updatemesresetting=0;
			master_busy = false;
			alert("Problem deleting credible set from db");
		}
	});
};

var allow_image_paste = false;
function AddDetailOverlay(sourcedocuments_row_id) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var row_id = sourcedocuments_row_id.replace("sourcedocumentsrow", "");
	allow_image_paste = true;
	var mydiv = document.getElementById("adddetailoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewdetaildiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">New Detail</p>';
	dcontent += '<p style="color:#000; vertical-align: middle;"><textarea id="new_detail_description" rows="4" cols="48" style="color:#000; vertical-align: middle;" placeholder="Enter description" class="ak_detail_overlay ak_tablecell" type="text"></textarea></p>';
	dcontent += '<p></p>';
	dcontent += '<p style="color:#000; vertical-align: middle;"><a href="javascript:;" class="ak_detail_overlay" id="ak_paste_a" ><div id="ak_dandd" style="width: 300px; height: 200px; display: inline-block; vertical-align: top;"><img id="ak_paste_img" class="ak_detail_overlay contain_img" width="300" height="200"></img></div></a></p>';
	dcontent += '<button id="ak_add_detail_button" type="button" class="ak_detail_overlay btn btn-primary btn-sm" onclick="SaveNewDetail('+row_id+')">Save</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_add_detail_x_button');
	tmpb.setAttribute('onclick', 'RemoveAddDetailOverlay()');
	tmpb.setAttribute('class', 'ak_detail_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	document.getElementById('ak_dandd').addEventListener('dragenter', akdragenter, false);
	document.getElementById('ak_dandd').addEventListener('dragover', akdragover, false);
	document.getElementById('ak_dandd').addEventListener('drop', akdrop, false);
	mydiv.style.display = 'block';
	master_busy = false;
};

RemoveAddDetailOverlay = function() {
	allow_image_paste = false;
	var mydiv = document.getElementById("adddetailoverlay");
	mydiv.style.display = 'none';
}

function akhandledandd(file) {
	var breader = new FileReader();
	breader.onload = function(evt) {
		$("#ak_paste_img").attr("src", evt.target.result);
		$("#ak_paste_a").attr("href", evt.target.result);
		$("#ak_paste_a").attr("data-lightbox", "ak_paste_a");
	}
	breader.readAsDataURL(file);
}

function akdragenter(e) {
	e.stopPropagation();
	e.preventDefault()
}

function akdragover(e) {
	e.stopPropagation();
	e.preventDefault()
}

function akdrop(e) {
	e.stopPropagation();
	e.preventDefault();
	akhandledandd(e.dataTransfer.files[0]);
}

document.addEventListener('paste', function(e) {paste_auto(e); }, false);
paste_auto = function(e) {
	if (allow_image_paste) {
		if (e.clipboardData) {
			var items = e.clipboardData.items;
			if (!items) return;
			for (var i=0; i<items.length; i++) {
				if (items[i].type.indexOf("image") == -1) continue;
				var blob = items[i].getAsFile();
				var breader = new FileReader();
				breader.onload = function(evt) {
					akload_image(evt.target.result); // evt.target.result contains the base64 data
				}
				breader.readAsDataURL(blob);
			}
		}
	}
};

function akload_image(base64_src) {
	$("#ak_paste_img").attr("src", base64_src);
	$("#ak_paste_a").attr("href", base64_src);
};

SaveNewDetail = function(sd_id) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_detail_overlay').attr('disabled', true);
	var thediv = document.getElementById('adddetailoverlay');
	var img_b64_text = thediv.getElementsByTagName("img")[0].src;
	var desc_text = thediv.getElementsByTagName("textarea")[0].value;
	var whattodo = {
			todo: "submitnewdetail",
			source_db_id: sd_id,
			desc: desc_text,
			img_b64: img_b64_text
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewdetail/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitnewdetail not successful at '+stamp);
				$('.ak_detail_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving new detail to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewdetail had user errors at '+stamp);
					$('.ak_detail_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$("#adddetail"+sd_id).replaceWith(data.trtext);
					$("#reftable").trigger('update');
					var rs = document.getElementById("sourcedocumentsrow"+sd_id).getElementsByTagName("td")[7].getAttribute('rowspan');
					rs++;
					document.getElementById("sourcedocumentsrow"+sd_id).getElementsByTagName("td")[7].setAttribute('rowspan', rs);
					var but = document.getElementById("sourcedocumentsrow"+sd_id).getElementsByClassName("detail-toggle")[0];
					but.firstChild.data = 'Hide Details ('+(rs-2)+')';
					allow_image_paste = false;
					$("#detail"+data.detaildbid+" .ak_detail_section_selector").select2({minimumResultsForSearch: 20});
					$('.ak_detail_overlay').attr('disabled', false);
					var mydiv = document.getElementById("adddetailoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitnewdetail not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_detail_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving new detail to db");
		}
	});
};

RemoveDetail = function(detailid) {
	var d_id = detailid.replace('detail', '');
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	$('.akdetailrowclass'+d_id).attr('disabled', true);
	master_busy = true;
	var whattodo = {
			todo: "removedetail",
			detail_id: d_id
	};
	$.ajax({
		url: "http://"+window.location.host+"/removedetail/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('removedetail not successful at '+stamp);
				$('.akdetailrowclass'+d_id).attr('disabled', false);
				master_busy = false;
				alert("Problem deleting detail from db");
			} else {
				if (data.old_section_lc!=="None") {
					$("#"+data.old_section_lc+"table").trigger('disablePager');
					$("#"+data.old_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
					$("#"+data.old_section_source_documents_id).replaceWith(data.old_section_revised_rows);
					$("#"+data.old_section_lc+"table").trigger('update');
					$("#"+data.old_section_lc+"table").trigger('enablePager');
					$("#"+data.old_section_lc+"table").find('tr').show(); // not sure why needed
				}
				$("#reftable").trigger('disablePager');
				$("#"+detailid).remove();
				var rs = document.getElementById("sourcedocumentsrow"+data.source_documents_id).getElementsByTagName("td")[7].getAttribute('rowspan');
				rs--;
				document.getElementById("sourcedocumentsrow"+data.source_documents_id).getElementsByTagName("td")[7].setAttribute('rowspan', rs);
				var but = document.getElementById("sourcedocumentsrow"+data.source_documents_id).getElementsByClassName("detail-toggle")[0];
				if (rs > 2) {
					but.firstChild.data = 'Hide Details ('+(rs-2)+')';
				} else {
					but.firstChild.data = 'Hide Details';
				}
				$("#reftable").trigger('enablePager');
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			console.log('removedetail not successful at '+stamp);
			$('.akdetailrowclass'+d_id).attr('disabled', false);
			master_busy = false;
			alert("Problem deleting detail from db");
		}
	});
};

var updatesectionresetting = 0; // not bulletproof since one variable used for all section assignments, but prob better than nothing
UpdateSection = function(selector) {
	if (updatesectionresetting==1) {return false;}
	var cl_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-csection");
	var n_val = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		updatesectionresetting=1;
		$(selector).val(c_val).trigger('change');
		updatesectionresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	var whattodo = {
			todo: "updatesection",
			new_section: n_val,
			detail_id: cl_id
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitdetailsectionassignment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitdetailsectionassignment not successful at '+stamp);
				updatesectionresetting=1;
				$(selector).val(c_val).trigger('change');
				updatesectionresetting=0;
				master_busy = false;
				alert("Problem saving new section assignment to db");
			} else {
				selector.setAttribute("data-csection", n_val);
				if (data.old_section_lc!=="none") {
					$("#"+data.old_section_lc+"detailstable").trigger('disablePager');
					$("#"+data.old_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
					$("#"+data.old_section_source_documents_id).replaceWith(data.old_section_revised_rows);
					$("#"+data.old_section_lc+"detailstable").trigger('update');
					$("#"+data.old_section_lc+"detailstable").trigger('enablePager');
					$("#"+data.old_section_lc+"detailstable").find('tr').show(); // not sure why needed
				}
				if (data.new_section_lc!=="none") {
					$("#"+data.new_section_lc+"detailstable").trigger('disablePager');
					if ( $("#"+data.new_section_source_documents_id).length ) {
						$("#"+data.new_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
						$("#"+data.new_section_source_documents_id).replaceWith(data.new_section_revised_rows);
					} else {
						$rows = $(data.new_section_revised_rows);
						$("#"+data.new_section_lc+"detailstable").find('tbody').append($rows);
					}
					$("#"+data.new_section_lc+"detailstable").trigger('update');
					$("#"+data.new_section_lc+"detailstable").trigger('enablePager');
					$("#"+data.new_section_lc+"detailstable").find('tr').show(); // not sure why needed
				}
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitdetailsectionassignment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			updatesectionresetting=1;
			$(selector).val(c_val).trigger('change');
			updatesectionresetting=0;
			master_busy = false;
			alert("Problem saving new section assignment to db");
		}
	});
};

function AddPubmedOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var mydiv = document.getElementById("addpubmedoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewpubmeddiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">New Pubmed</p>';
	dcontent += '<p style="color:#000;">Pubmed ID: <input class="ak_pubmed_overlay" id="new_pubmed_id" style="text-align:center;width:250px;color:#000;" placeholder="Eg. 23817569" type="text" maxlength="15"></p>';
	dcontent += '<p></p>';
	dcontent += '<button id="ak_add_pubmed_button" type="button" class="ak_pubmed_overlay btn btn-primary btn-sm" onclick="SaveNewPubmed()">Save</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_add_pubmed_x_button');
	tmpb.setAttribute('onclick', 'RemoveAddPubmedOverlay()');
	tmpb.setAttribute('class', 'ak_pubmed_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	mydiv.style.display = 'block';
	master_busy = false;
	return;
};

RemoveAddPubmedOverlay = function() {
	var mydiv = document.getElementById("addpubmedoverlay");
	mydiv.style.display = 'none';
}

SaveNewPubmed = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_pubmed_overlay').attr('disabled', true);
	var pubmed_id_text = document.getElementById("new_pubmed_id").value;
	var whattodo = {
			todo: "submitnewpubmed",
			pubmed_id: pubmed_id_text
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewpubmed/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitnewpubmed not successful at '+stamp);
				$('.ak_pubmed_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving Pubmed to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewpubmed had user errors at '+stamp);
					$('.ak_pubmed_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$trs = $(data.trtext);
					$("#reftable")
					.find('tbody').append($trs)
					.trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+data.sourcedocumentsid).replaceWith(data.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+data.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_pubmed_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addpubmedoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitnewpubmed not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_pubmed_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving Pubmed to db");
		}
	});
};

function AddBiorxivOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var mydiv = document.getElementById("addbiorxivoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewbiorxivdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">New bioRxiv</p>';
	dcontent += '<p style="color:#000;">bioRxiv ID: <input class="ak_biorxiv_overlay" id="new_biorxiv_id" style="text-align:center;width:250px;color:#000;" placeholder="Eg. 10.1101/045831" type="text" maxlength="15"></p>';
	dcontent += '<p></p>';
	dcontent += '<button id="ak_add_biorxiv_button" type="button" class="ak_biorxiv_overlay btn btn-primary btn-sm" onclick="SaveNewBiorxiv()">Save</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_add_biorxiv_x_button');
	tmpb.setAttribute('onclick', 'RemoveAddBiorxivOverlay()');
	tmpb.setAttribute('class', 'ak_biorxiv_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	mydiv.style.display = 'block';
	master_busy = false;
	return;
};

RemoveAddBiorxivOverlay = function() {
	var mydiv = document.getElementById("addbiorxivoverlay");
	mydiv.style.display = 'none';
}

SaveNewBiorxiv = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_biorxiv_overlay').attr('disabled', true);
	var doi_text = document.getElementById("new_biorxiv_id").value;
	var whattodo = {
			todo: "submitnewbiorxiv",
			doi: doi_text
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewbiorxiv/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitnewbiorxiv not successful at '+stamp);
				$('.ak_biorxiv_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving bioRxiv to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewbiorxiv had user errors at '+stamp);
					$('.ak_biorxiv_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$trs = $(data.trtext);
					$("#reftable")
					.find('tbody').append($trs)
					.trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+data.sourcedocumentsid).replaceWith(data.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+data.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_biorxiv_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addbiorxivoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitnewbiorxiv not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_biorxiv_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving bioRxiv to db");
		}
	});
};

function AddFileOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var mydiv = document.getElementById("addfileoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewfilediv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">New File</p>';
	dcontent += '<p style="color:#000;">Year: <input class="ak_file_overlay" id="new_file_year" style="text-align:center;width:125px;color:#000;" placeholder="Eg. 2018" type="text" maxlength="4"></p>';
	dcontent += '<p style="color:#000;"><input id="new_file_file" class="ak_file_overlay" style="text-align:center;width:250px" type="file" onchange="RecoverB64RefFile(this)"></p>';
	dcontent += '<p style="color:#000; vertical-align: middle;"><textarea id="new_file_description" rows="4" cols="48" style="color:#000; vertical-align: middle;" placeholder="Enter description" class="ak_file_overlay ak_tablecell" type="text"></textarea></p>';
	dcontent += '<p></p>';
	dcontent += '<button id="ak_add_file_button" type="button" class="ak_file_overlay btn btn-primary btn-sm" onclick="SaveNewFile()">Save</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_add_file_x_button');
	tmpb.setAttribute('onclick', 'RemoveAddFileOverlay()');
	tmpb.setAttribute('class', 'ak_file_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	mydiv.style.display = 'block';
	master_busy = false;
	return;
};

RemoveAddFileOverlay = function() {
	var mydiv = document.getElementById("addfileoverlay");
	mydiv.style.display = 'none';
}

function RecoverB64RefFile(inputelement) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	if (inputelement.files.length>0) {
		var breader = new FileReader();
		breader.onload = function(evt) {
			$("#new_file_file").attr("data-akfilecontentsb64", evt.target.result);
		}
		breader.readAsDataURL(inputelement.files[0]);
	} else {
		$("#new_file_file").attr("data-akfilecontentsb64", "");
	}
}

SaveNewFile = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_file_overlay').attr('disabled', true);
	var new_file_year = document.getElementById("new_file_year").value;
	var new_file_name = document.getElementById("new_file_file").value.split(/[\\\\/]/).pop();
	var new_file_description = document.getElementById("new_file_description").value;
	var b64contents = $("#new_file_file").attr("data-akfilecontentsb64");
	var whattodo = {
			todo: "submitnewfile",
			file_year: new_file_year,
			file_name: new_file_name,
			file_description: new_file_description,
			file_contents: b64contents
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewfile/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitnewfile not successful at '+stamp);
				$('.ak_file_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving file to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewfile had user errors at '+stamp);
					$('.ak_file_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$trs = $(data.trtext);
					$("#reftable")
					.find('tbody').append($trs)
					.trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+data.sourcedocumentsid).replaceWith(data.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+data.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_file_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addfileoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitnewfile not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_file_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving file to db");
		}
	});
};

function AddWebOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var mydiv = document.getElementById("addweboverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewwebdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += '<p style="color:#000; font-size: 20px;">New Web</p>';
	dcontent += '<p style="color:#000;">Year: <input class="ak_web_overlay" id="new_web_year" style="text-align:center;width:125px;color:#000;" placeholder="Eg. 2018" type="text" maxlength="4"></p>';
	dcontent += '<p style="color:#000;"><input id="new_web_site" placeholder="https://www.google.com" class="ak_web_overlay" style="text-align:center;width:450px" type="list" list="siteList"></p>';
	dcontent += '<datalist id="siteList">';
	dcontent += '<option value></option>';
	dcontent += '<option value="https://scholar.google.com">google scholar</option>';
	dcontent += '<option value="http://biogps.org/#goto=welcome">biogps link</option>';
	dcontent += '<option value="http://www.gtexportal.org">gtex link</option>';
	dcontent += '<option value="https://www.immunobase.org">immunobase link</option>';
	dcontent += '<option value="http://www.omim.org">omim link</option>';
	dcontent += '<option value="http://www.proteinatlas.org">proteinatlas link</option>';
	dcontent += '<option value="https://grasp.nhlbi.nih.gov/Search.aspx">grasp link</option>';
	dcontent += '<option value="http://www.orpha.net">orphanet link</option>';
	dcontent += '<option value="http://structure.bmc.lu.se/idbase/IDRefSeq/xml/idr/genes.shtml">idr link</option>';
	dcontent += '<option value="http://www.ncbi.nlm.nih.gov/gap/phegeni">phegeni link</option>';
	dcontent += '</datalist>';
	dcontent += '</p>';
	dcontent += '<p style="color:#000; vertical-align: middle;"><textarea id="new_web_description" rows="4" cols="48" style="color:#000; vertical-align: middle;" placeholder="Enter description" class="ak_web_overlay ak_tablecell" type="text"></textarea></p>';
	dcontent += '<p></p>';
	dcontent += '<button id="ak_add_web_button" type="button" class="ak_web_overlay btn btn-primary btn-sm" onclick="SaveNewWeb()">Save</button>';
	dcontent += '<p style="color:#000; font-size: 20px;"></p>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_add_web_x_button');
	tmpb.setAttribute('onclick', 'RemoveAddWebOverlay()');
	tmpb.setAttribute('class', 'ak_web_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	mydiv.style.display = 'block';
	master_busy = false;
	return;
};

RemoveAddWebOverlay = function() {
	var mydiv = document.getElementById("addweboverlay");
	mydiv.style.display = 'none';
}

SaveNewWeb = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_web_overlay').attr('disabled', true);
	var new_web_year = document.getElementById("new_web_year").value;
	var new_web_site = document.getElementById("new_web_site").value;
	var new_web_description = document.getElementById("new_web_description").value;
	var whattodo = {
			todo: "submitnewweb",
			web_year: new_web_year,
			web_site: new_web_site,
			web_description: new_web_description
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewweb/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok!="ok") {
				console.log('submitnewweb not successful at '+stamp);
				$('.ak_web_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving site to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewweb had user errors at '+stamp);
					$('.ak_web_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					$trs = $(data.trtext);
					$("#reftable")
					.find('tbody').append($trs)
					.trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+data.sourcedocumentsid).replaceWith(data.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+data.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+data.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_web_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addweboverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('submitnewweb not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_web_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving site to db");
		}
	});
};

RemoveRefRow = function(whichdbid) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var row_id = whichdbid.replace("sourcedocumentsrow", "");
	$('.akrefrowclass'+row_id).attr('disabled', true);
	var whattodo = {
			todo: "oktoremoveref",
			sd_db_id: row_id
	};
	$.ajax({
		url: "http://"+window.location.host+"/oktoremoveref/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				if (data.problems=="") {
					$("#reftable").trigger('disablePager');
					if (document.getElementById("adddetail"+row_id)) {
						$("#adddetail"+row_id).remove();
					}
					$("#"+whichdbid).remove();
					$("#reftable").trigger('enablePager');
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+data.unreviewed_count+' out of '+data.total_count+' not yet reviewed)';
				} else {
					$('.akrefrowclass'+row_id).attr('disabled', false);
					alert(data.problems);
				}
				master_busy = false;
			} else {
				console.log('problem removing reference at '+stamp);
				$('.akrefrowclass'+row_id).attr('disabled', false);
				master_busy = false;
				alert("Problem removing reference");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			console.log('problem removing reference at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.akrefrowclass'+row_id).attr('disabled', false);
			master_busy = false;
			alert("Problem removing reference");
		}
	});
};

function AddGWASOverlay(ispqtl) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var url = "http://"+window.location.host+"/get_pub_list/"+encodeURIComponent(gene_symbol);
	var to_use = "";
	var jqxhr = $.getJSON(url, function() {
	}).done(function(data) {
		var stamp = aktimeStamp();
		if (data.isok=="ok") {
			var pobj = JSON.parse(data.pub_list);
			var mydiv = document.getElementById("addgwasoverlay");
			mydiv.innerHTML = '';
			var dcontent = '<div id="aknewgwasdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
			dcontent += '<p style="color:#000; font-size: 20px;"></p>';
			if (ispqtl==0) {
				dcontent += '<p style="color:#000; font-size: 20px;">New Association</p>';
			} else {
				dcontent += '<p style="color:#000; font-size: 20px;">New pQTL</p>';
			}
			dcontent += '<p style="color:#000;">Phenotype: <input class="ak_gwas_overlay" id="new_gwas_phenotype" style="text-align:center;width:250px;color:#000;" placeholder="Eg. Schizophrenia" type="text" maxlength="150"></p>';
			dcontent += '<p style="color:#000;">Source:';
			var s_html = '<select style="width:400px" class="ak_gwas_overlay ak_gwas_source_selector" >';
			s_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
			for (var i=0; i<pobj.length; ++i) {
				s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].column_display_text)+'</option>';
			}
			s_html += '</select>';
			dcontent += s_html+'</p>';
			dcontent += '<p style="color:#000;">Index variant: <input class="ak_gwas_overlay" id="new_gwas_indexvariant" style="text-align:center;width:105px;color:#000;" placeholder="Eg. rs123" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">Allele (optional): <input class="ak_gwas_overlay" id="new_gwas_allele" style="text-align:center;width:105px;color:#000;" placeholder="Eg. T" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">Pvalue: <input class="ak_gwas_overlay" id="new_gwas_pvalue" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 3.0e-13" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000;">OR/Beta: <input class="ak_gwas_overlay" id="new_gwas_orbeta" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 1.11" type="text" maxlength="15"></p>';
			dcontent += '<p style="color:#000; vertical-align: middle;">Comment: <textarea id="new_gwas_comment" rows="4" cols="50" style="color:#000; vertical-align: middle;" placeholder="Enter comment" class="ak_gwas_overlay ak_tablecell" type="text"></textarea></p>';
			dcontent += '<p></p>';
			dcontent += '<button id="ak_add_gwas_button" type="button" class="ak_gwas_overlay btn btn-primary btn-sm" onclick="SaveNewGWAS('+ispqtl+')">Save</button>';
			dcontent += '<p style="color:#000; font-size: 20px;"></p>';
			dcontent += "</div>";
			mydiv.innerHTML = dcontent;
			var tmpb = document.createElement('button');
			tmpb.setAttribute('id', 'ak_add_gwas_x_button');
			tmpb.setAttribute('onclick', 'RemoveAddGWASOverlay()');
			tmpb.setAttribute('class', 'ak_gwas_overlay btn btn-primary btn-sm');
			tmpb.style.verticalAlign='top';
			tmpb.style.display='inline-block';
			tmpb.innerHTML = 'X';
			mydiv.appendChild(tmpb);
			$(".ak_gwas_source_selector").select2();
			mydiv.style.display = 'block';
			master_busy = false;
		} else {
			console.log('problem recovering references at '+stamp);
			master_busy = false;
			alert("Problem recovering references");
		}
		return;
	})
	.fail(function(xhr, desc, err) {
		master_busy = false;
		alert("Problem recovering references");
		return;
	});
};

RemoveAddGWASOverlay = function() {
	var mydiv = document.getElementById("addgwasoverlay");
	mydiv.style.display = 'none';
}

SaveNewGWAS = function(ispqtl) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_gwas_overlay').attr('disabled', true);
	var therow = document.getElementById("new_gwas_row");
	var pheno_text = document.getElementById("new_gwas_phenotype").value;
	var index_variant_text = document.getElementById("new_gwas_indexvariant").value;
	var pvalue_text = document.getElementById("new_gwas_pvalue").value;
	var or_beta_text = document.getElementById("new_gwas_orbeta").value;
	var allele_text = document.getElementById("new_gwas_allele").value;
	var source_selector = document.getElementsByClassName("ak_gwas_source_selector")[0];
	var source_id = -1;
	if (source_selector.selectedIndex>-1) {
		source_id = source_selector.options[source_selector.selectedIndex].value;
	}
	var comment_text = document.getElementById("new_gwas_comment").value;
	var whattodo = {
			todo: "submitnewgwasrow",
			phenotype: pheno_text,
			source_db_id: source_id,
			index_variant: index_variant_text,
			allele: allele_text,
			pvalue: pvalue_text,
			or_beta: or_beta_text,
			comment: comment_text,
			is_pqtl: ispqtl
	};
	$.ajax({
		url: "http://"+window.location.host+"/submitnewgwasrow/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			//console.log(data);
			if (data.isok!="ok") {
				console.log('submitnewgwasrow not successful at '+stamp);
				$('.ak_gwas_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving GWAS/pQTL to db");
			} else {
				if (data.errors!="") {
					console.log('submitnewgwasrow had user errors at '+stamp);
					$('.ak_gwas_overlay').attr('disabled', false);
					master_busy = false;
					alert(data.errors);
				} else {
					if (ispqtl==0) {$("#gwastable").trigger('addRows', [data.trtext]);}
					else {$("#pqtltable").trigger('addRows', [data.trtext]);}
					
					$("#gwasrow"+data.gwasdbid+" .ak_mes_selector").select2({minimumResultsForSearch: 20});
					$("#gwasrow"+data.gwasdbid+" .ak_gwas_comment").attr('data-curval', $("#gwasrow"+data.gwasdbid+" .ak_gwas_comment").val());
					$("#gwasrow"+data.gwasdbid+" .ak_gwas_sdn").attr('data-curval', $("#gwasrow"+data.gwasdbid+" .ak_gwas_sdn").val());
					$('.ak_gwas_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addgwasoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('problem with submitnewgwasrow at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_gwas_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving new GWAS/pQTL entry to db");
		}
	});
};

RemoveGWASRow = function(whichdbid, ispqtl) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	whichdbid = whichdbid.replace("gwasrow", "");
	$('.akgwasrowclass'+whichdbid).attr('disabled', true);
	var whattodo = {
			todo: "removegwasrow",
			gwas_db_id: whichdbid,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/removegwasrow/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				var eqtllist = JSON.parse(data.eqtl_ids);
				var overlap_counts = JSON.parse(data.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				if (ispqtl==0) {
					$("#gwastable").trigger('disablePager');
					$("#gwasrow"+whichdbid).remove();
					$("#gwastable").trigger('enablePager');
				} else {
					$("#pqtltable").trigger('disablePager');
					$("#gwasrow"+whichdbid).remove();
					$("#pqtltable").trigger('enablePager');
				}
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svggwas_'+whichdbid) {
						clear_supl_div();
					}
					if (shown_supl_id.indexOf('GR'+whichdbid+'-')>-1) {
						if (shown_supl_id=='GR'+whichdbid+'-') {clear_supl_div();}
						else {
							var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
							var sa = trimmed.split("-");
							var ix;
							for (ix=0; ix < sa.length; ++ix) {
								var svge = document.getElementById(sa[ix]);
								if (svge!=null) {
									var supl = svge.getAttribute("data-supl");
									var classname = svge.getAttribute("data-class");
									var ldgids = svge.getAttribute("data-ldgids");
									fill_supl_div(supl, ldgids, '3', classname);
									break;
								}
							}
						}
					} else {
						if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
							var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
							var sa = trimmed.split("-");
							var ix;
							for (ix=0; ix < sa.length; ++ix) {
								var svge = document.getElementById(sa[ix]);
								if (svge!=null) {
									var classname = svge.getAttribute("data-class");
									$("."+classname).each(function(i, obj) {
										obj.children[0].setAttribute('stroke-width', '3');
									});
									break;
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				console.log('problem with removegwasrow at '+stamp);
				$('.akgwasrowclass'+whichdbid).attr('disabled', false);
				master_busy = false;
				alert("Problem removing GWAS/pQTL entry from db");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('problem with removegwasrow at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.akgwasrowclass'+whichdbid).attr('disabled', false);
			master_busy = false;
			alert("Problem removing GWAS/pQTL entry from db");
		}
	});
};

UpdateGWASShowHide = function(cbox) {
	var db_id = cbox.getAttribute("data-dbid");
	var s_or_h = "Hide";
	if (cbox.checked) { s_or_h = "Show";}
	if (master_busy) {
		if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "updategwasshowhide",
			show_or_hide: s_or_h,
			gwas_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updategwasshowhide/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				var eqtllist = JSON.parse(data.eqtl_ids);
				var overlap_counts = JSON.parse(data.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svggwas_'+db_id) {
						var svge = document.getElementById('svggwas_'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							fill_supl_div(supl, 'svggwas_'+db_id);
						} else {
							clear_supl_div();
						}
					}
					if (shown_supl_id.indexOf('GR'+db_id+'-')>-1) {
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id=='GR'+db_id+'-') {clear_supl_div();}
							else {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var supl = svge.getAttribute("data-supl");
										var classname = svge.getAttribute("data-class");
										var ldgids = svge.getAttribute("data-ldgids");
										fill_supl_div(supl, ldgids, '3', classname);
										break;
									}
								}
							}
						}
					} else {
						var updateit = 0;
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var classname = svge.getAttribute("data-class");
							$("."+classname).each(function(i, obj) {
								var oid = obj.getAttribute('id');
								if (shown_supl_id.indexOf(oid+'-')>-1) {
									updateit=1;
									return false;
								}
							});
						}
						if (updateit==1) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('problem with updategwasshowhide at '+stamp);
				if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
				master_busy = false;
				alert("Problem showing/hiding GWAS/pQTL result");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('problem with updategwasshowhide at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
			master_busy = false;
			alert("Problem showing/hiding GWAS/pQTL result");
		}
	});
};

UpdateShowHideNonCoding = function(cbox) {
	var s_or_h = 0;
	if (cbox.checked) { s_or_h = 1;}
	if (master_busy) {
		if (s_or_h==1) {cbox.checked = false;} else {cbox.checked = true;}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "updateshowhidenoncoding",
			hidenoncoding: s_or_h,
			svg_display_mode: SVGMode
	};
	$.ajax({
		url: "http://"+window.location.host+"/updateshowhidenoncoding/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					hide_non_coding = s_or_h;
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('problem with updategwasshowhidenoncoding at '+stamp);
				if (s_or_h==1) {cbox.checked = false;} else {cbox.checked = true;}
				master_busy = false;
				alert("Problem showing/hiding non-coding genes");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('problem with updateshowhidenoncoding at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			if (s_or_h==1) {cbox.checked = false;} else {cbox.checked = true;}
			master_busy = false;
			alert("Problem showing/hiding non-coding genes");
		}
	});
};

UpdateMarkerForLD = function(cbox) {
	var vm_db_id = cbox.getAttribute("data-dbid");
	var add_or_rem = "Remove";
	if (cbox.checked) { add_or_rem = "Add"; $('.'+cbox.className).prop('checked', true);}
	else { $('.'+cbox.className).prop('checked', false);}
	if (master_busy) {
		if (add_or_rem=='Add') {cbox.checked = false;} else {cbox.checked = true;}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "updatemarkerforld",
			add_or_remove: add_or_rem,
			vm_db_id: vm_db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updatemarkerforld/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (add_or_rem=='Remove') {
						var marker_was_shown = 0;
						var remaining_id = null;
						var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
						var sa = trimmed.split("-");
						var ix;
						for (ix=0; ix < sa.length; ++ix) {
							if (sa[ix].endsWith(':'+vm_db_id)) {
								marker_was_shown=1;
							} else {
								remaining_id = sa[ix];
							}
						}
						if (marker_was_shown==1 && remaining_id==null) {
							clear_supl_div();
						} else if (marker_was_shown==1 && remaining_id!=null) {
							var svge = document.getElementById(remaining_id);
							if (svge!=null) {
								var supl = svge.getAttribute("data-supl");
								var classname = svge.getAttribute("data-class");
								var ldgids = svge.getAttribute("data-ldgids");
								fill_supl_div(supl, ldgids, '3', classname);
							}
						} else {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
					if (add_or_rem=='Add') {
						var classname;
						var updated=0;
						$(".m"+vm_db_id).each(function(i, obj) {
							classname = obj.getAttribute('data-class');
							return false;
						});
						$("."+classname).each(function(i, obj) {
							var oid = obj.getAttribute('id');
							if (shown_supl_id.indexOf(oid+'-')>-1) {
								var supl = obj.getAttribute("data-supl");
								var classname = obj.getAttribute("data-class");
								var ldgids = obj.getAttribute("data-ldgids");
								fill_supl_div(supl, ldgids, '3', classname);
								updated=1;
								return false;
							}
						});
						if (updated==0) {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				if (add_or_rem=='Remove') {cbox.checked = true;} else {cbox.checked = false;}
				console.log('problem updating marker for ld at '+stamp);
				master_busy = false;
				alert("Problem adding/removing marker from LD display");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			if (add_or_rem=='Remove') {cbox.checked = true;} else {cbox.checked = false;}
			console.log('problem updating marker for ld at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem adding/removing marker from LD display");
		}
	});
};

var updatemesresetting = 0; // not bulletproof since one variable used for all mes assignments, but prob better than nothing
UpdateGWASMarkerEquivalenceSet = function(selector) {
	if (updatemesresetting==1) {return false;}
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-cmes");
	var n_val = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		updatemesresetting=1;
		$(selector).val(c_val).trigger('change');
		updatemesresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	if (n_val=='Add credible set') {
		updatemesresetting=1;
		$(selector).val(c_val).trigger('change');
		updatemesresetting=0;
		CreateCredibleSetOverlay(selector);
		master_busy = false;
		return false;
	}
	if (n_val=='Delete credible set') {
		updatemesresetting=1;
		$(selector).val(c_val).trigger('change');
		DeleteCredibleSet(selector);

		return false;
	}
	var whattodo = {
			todo: "updategwasmarkerequivalenceset",
			marker_equivalence_set: n_val,
			gwas_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding
	};
	$.ajax({
		url: "http://"+window.location.host+"/updategwasmarkerequivalenceset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			if (data.isok=="ok") {
				var eqtllist = JSON.parse(data.eqtl_ids);
				var overlap_counts = JSON.parse(data.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				selector.setAttribute("data-cmes", n_val);
				if (data.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(data.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svggwas_'+db_id) {
						var svge = document.getElementById('svggwas_'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							fill_supl_div(supl, 'svggwas_'+db_id);
						} else {
							clear_supl_div();
						}
					}
					if (shown_supl_id.indexOf('GR'+db_id+'-')>-1) {
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id=='GR'+db_id+'-') {clear_supl_div();}
							else {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var supl = svge.getAttribute("data-supl");
										var classname = svge.getAttribute("data-class");
										var ldgids = svge.getAttribute("data-ldgids");
										fill_supl_div(supl, ldgids, '3', classname);
										break;
									}
								}
							}
						}
					} else {
						var updateit = 0;
						var svge = document.getElementById('GR'+db_id);
						if (svge!=null) {
							var classname = svge.getAttribute("data-class");
							$("."+classname).each(function(i, obj) {
								var oid = obj.getAttribute('id');
								if (shown_supl_id.indexOf(oid+'-')>-1) {
									updateit=1;
									return false;
								}
							});
						}
						if (updateit==1) {
							var supl = svge.getAttribute("data-supl");
							var classname = svge.getAttribute("data-class");
							var ldgids = svge.getAttribute("data-ldgids");
							fill_supl_div(supl, ldgids, '3', classname);
						} else {
							if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
								var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
								var sa = trimmed.split("-");
								var ix;
								for (ix=0; ix < sa.length; ++ix) {
									var svge = document.getElementById(sa[ix]);
									if (svge!=null) {
										var classname = svge.getAttribute("data-class");
										$("."+classname).each(function(i, obj) {
											obj.children[0].setAttribute('stroke-width', '3');
										});
										break;
									}
								}
							}
						}
					}
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('updategwasmarkerequivalenceset not successful at '+stamp);
				updatemesresetting=1;
				$(selector).val(c_val).trigger('change');

				updatemesresetting=0;
				master_busy = false;
				alert("Problem updating GWAS Marker Equivalence Set");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('updategwasmarkerequivalenceset not successful at '+stamp);
			updatemesresetting=1;
			$(selector).val(c_val).trigger('change');
			updatemesresetting=0;
			master_busy = false;
			alert("Problem updating GWAS Marker Equivalence Set");
		}
	});
};

var entityMap = { 
		'&': '&amp;',
		'<': '&lt;',
		'>': '&gt;',
		'"': '&quot;',
		"'": '&#39;',
		'/': '&#x2F;',
		'`': '&#x60;',
		'=': '&#x3D;',
};

var reventityMap = { 
		'&amp;': '&',
		'&lt;': '<',
		'&gt;': '>',
		'&quot;': '"',
		'&#39;': "'",
		'&#x2F;': '/',
		'&#x60;': '`',
		'&#x3D;': '=',
};

function escapeHtml (string) {
	return String(string).replace(/[&<>"'`=/]/g, function (s) {return entityMap[s];});
}

function revescapeHtml(string) {
	return String(string).replace( /(&amp;|&lt;|&gt;|&quot;|&#39;|&#x2F;|&#x60;|&#x3D;)/g, function (s) {return entityMaprev[s];});
}

window.onload = function() {
	if (typeof svginit === "function") {
		svginit();
	}
	document.addEventListener("dragenter", function(e) {
		if (e.target.id != 'ak_dandd') {
			e.preventDefault();
			e.dataTransfer.effectAllowed = 'none';
			e.dataTransfer.dropEffect = 'none';
		}
	}, false);
	document.addEventListener("dragover", function(e) {
		if (e.target.id != 'ak_dandd') {
			e.preventDefault();
			e.dataTransfer.effectAllowed = 'none';
			e.dataTransfer.dropEffect = 'none';
		}
	}, false);
	document.addEventListener("drop", function(e) {
		if (e.target.id != 'ak_dandd') {
			e.preventDefault();
			e.dataTransfer.effectAllowed = 'none';
			e.dataTransfer.dropEffect = 'none';
		}
	}, false);
	document.body.onmousedown = function() { 
		if (typeof svginit === "function") {
			++akmouseDown;
		}
	}
	document.body.onmouseup = function() {
		if (typeof svginit === "function") {
			--akmouseDown;
		}
	}
};

var shown_supl_id = '-1';
fill_supl_div = function(supl, supl_id, supltype, classname) {
	var mydiv = document.getElementById("ak_supl_div");
	clear_supl_div();
	if (supl==null) {return;} // weird but we'll protect against this

	mydiv.innerHTML = supl;
	if (supl_id.startsWith('CV') || supl_id.startsWith('GR') || supl_id.startsWith('ER')) {
		$("."+classname).each(function(i, obj) {
			obj.children[0].setAttribute('stroke-width', '3');
		});
	}
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_supl_button');
	tmpb.setAttribute('onclick', 'clear_supl_div()');
	tmpb.setAttribute('class', 'btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	shown_supl_id = supl_id;
	if (supltype=='1') {$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['97px','85px','85px']}});}
	else if (supltype=='2') {$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['97px','85px','85px','85px','85px']}});}
	else if (supltype=='3') {$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['97px','100px','85px','83px','120px', '125px', '135px', '70px','88px']}});}
};

clear_supl_div = function() {
	if (shown_supl_id!='-1') {
		if (shown_supl_id.startsWith('CV') || shown_supl_id.startsWith('GR') || shown_supl_id.startsWith('ER')) {
			var trimmed = shown_supl_id.substring(0, shown_supl_id.length -1);
			var sa = trimmed.split("-");
			var ix;
			for (ix=0; ix < sa.length; ++ix) {
				var svge = document.getElementById(sa[ix]);
				if (svge!=null) {
					var classname = svge.getAttribute("data-class");
					$("."+classname).each(function(i, obj) {
						obj.children[0].setAttribute('stroke-width', '1');
					});
					break;
				}
			}
		}
		var mydiv = document.getElementById("ak_supl_div");
		var akta = document.getElementById("aktmpdiv");
		var aktab = document.getElementById("ak_supl_button");
		mydiv.removeChild(akta);
		mydiv.removeChild(aktab);
		shown_supl_id='-1';
	}
};


function UpdateSVGMode() {
	SVGMode = $('input[name=svg_display_mode]:checked').val();
	if (master_busy) {
		if (SVGMode=='LD Summary') {
			$('#group_for_mes').css('visibility', 'visible');
			$('#group_for_ld').css('visibility', 'hidden');
		} else {
			$('#group_for_mes').css('visibility', 'hidden');
			$('#group_for_ld').css('visibility', 'visible');
		}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	if (SVGMode=='Credible Sets') {
		$('#group_for_mes').css('visibility', 'visible');
		$('#group_for_ld').css('visibility', 'hidden');
	} else {
		$('#group_for_mes').css('visibility', 'hidden');
		$('#group_for_ld').css('visibility', 'visible');
	}
	clear_supl_div();
	master_busy = false;
};

DownloadFile = function(whichdbid) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var row_id = whichdbid.replace("sourcedocumentsrow", "");
	var whattodo = {
			todo: "downloadfile",
			sd_db_id: row_id
	};
	$.ajax({
		url: "http://"+window.location.host+"/downloadfile/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(data){
			var stamp = aktimeStamp()
			if (data.isok=="ok") {
				var byteString = atob(data.b64.split(',')[1]);
				var mimeString = data.b64.split(',')[0].split(':')[1].split(';')[0];
				var ab = new ArrayBuffer(byteString.length);
				var ia = new Uint8Array(ab);
				for (var i = 0; i < byteString.length; i++) {
					ia[i] = byteString.charCodeAt(i);
				}
				var blob = new Blob([ab], {type: mimeString});
				saveAs(blob, data.name);
				master_busy = false;
			} else {
				console.log('download not successful at '+stamp);
				master_busy = false;
				alert("Problem downloading file");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			console.log('download not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem downloading file");
		}
	});
};

