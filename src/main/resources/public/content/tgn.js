"use strict;"

function UnreviewedCheckin() {
	var whattodo = {
			todo: "unreviewedcheckin",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/unreviewedcheckin",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok!="ok") {
				console.log('problem recovering unreviewed counts');
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem recovering unreviewed counts");
				}
			} else {
				var gobj = JSON.parse(returnedbyserver.gene_list);
				for (var i=0; i<gobj.length; ++i) {	
					var idchk = 'unrev_'+gobj[i].gene;
					var myele = document.getElementById(idchk);
					if (myele!=null) {	
						myele.innerHTML = gobj[i].unreviewed;
					}
				}
				$('#available_notebooks').trigger('update');
			}
			setTimeout(UnreviewedCheckin, 300000);	
		},
		error: function(xhr, textStatus, errorThrown){
			alert("Problem recovering unreviewed counts");
			setTimeout(UnreviewedCheckin, 300000);	
		}
	});
};

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

var comment_timerchecks = {
	"detail": [],
	"gwas": [],
	"eqtl": [],
	"source": [],
	"genephen": []
}

var comment_timercheckids = {
	"detail": [],
	"gwas": [],
	"eqtl": [],
	"source": [],
	"genephen": []
}

function startcommenttimer(textarea, origin) {
	var db_id = textarea.getAttribute("data-dbid");
	var fromwhere = '';
	if (textarea.hasAttribute("data-fromwhere")) {
		fromwhere = textarea.getAttribute("data-fromwhere");
	}
	var index = -1;
	for (var i=0; i<comment_timercheckids[origin].length; ++i) {
		if (comment_timercheckids[origin][i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(comment_timerchecks[origin][index]);
		comment_timerchecks[origin][index] = setTimeout(function() {updatecomment(db_id, origin, textarea.value, fromwhere)}, 2000);
	} else {
		comment_timercheckids[origin].push(db_id);
		var tmp =  setTimeout(function() {updatecomment(db_id, origin, textarea.value, fromwhere)}, 2000);
		comment_timerchecks[origin].push(tmp);
	}
};

function startupdatesummarytimer() {
	if (updatesummarycheck!=null) {
		clearTimeout(updatesummarycheck);
	}
	updatesummarycheck = setTimeout(function() {UpdateSummary()}, 2000);
};

var sdn_timerchecks = {
	"gwas": [],
	"eqtl": []
}

var sdn_timercheckids = {
	"gwas": [],
	"eqtl": []
}

function startsvgdisplaynametimer(inputelement,origin) {
	var db_id = inputelement.getAttribute("data-dbid");
	var index = -1;
	for (var i=0; i<sdn_timercheckids[origin].length; ++i) {
		if (sdn_timercheckids[origin][i]==db_id) {
			index = i;
			break;
		}
	}
	if (index>-1) {
		clearTimeout(sdn_timerchecks[origin][index]);
		sdn_timerchecks[origin][index] = setTimeout(function() {updatesvgdisplayname(db_id, origin, inputelement.value)}, 2000);
	} else {
		sdn_timercheckids[origin].push(db_id);
		var tmp =  setTimeout(function() {updatesvgdisplayname(db_id, origin, inputelement.value)}, 2000);
		sdn_timerchecks[origin].push(tmp);
	}
};

var updatesummarycheck = null;
UpdateSummary = function() {
	if (master_busy) {
		clearTimeout(updatesummarycheck);
		updatesummarycheck = setTimeout(function() {UpdateSummary()}, 2000);
		return;
	}
	master_busy = true;
	updatesummarycheck = null;
	var c_val = $( '#aksummaryarea' ).attr('data-curval');
	var n_val = $( '#aksummaryarea' ).val();
	var whattodo = {
			todo: "updatesummary",
			summary: n_val,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatesummary/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				$('#aksummaryarea').val(c_val);
				console.log('updatesummary not successful at '+stamp);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before changing the summary.");
				} else {
					alert("Problem saving summary to db");
				}
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

updatecomment = function(dbid, origin, newcomment, fromwhere) {
	var index = -1;
	for (var i=0; i<comment_timercheckids[origin].length; ++i) {
		if (comment_timercheckids[origin][i]==dbid) {
			index = i;
			break;
		}
	}
	if (index==-1) {
		return; // should not happen
	}
	if (master_busy) {
		clearTimeout(comment_timerchecks[origin][index]);
		comment_timerchecks[origin][index] = setTimeout(function() {updatecomment(dbid, origin, newcomment, fromwhere)}, 2000);
		return;
	}
	master_busy = true;
	comment_timerchecks[origin].splice(index,1);
	comment_timercheckids[origin].splice(index,1);
	
	var el_chk = null;
	var c_val;
	if (origin=='detail') {
		if (fromwhere==1) {
			el_chk = document.getElementById("detailrow"+dbid);
		} else {
			el_chk = document.getElementById("detailrowsection"+dbid);
		}
		if (el_chk!=null) {
			c_val = el_chk.getAttribute("data-curval");
		}
	} else if (origin=='gwas') {
		el_chk = document.getElementById("gwasrow"+dbid);
		if (el_chk!=null) {
			c_val = $("#gwasrow"+dbid+" .ak_gwas_comment").attr('data-curval');
		}
	} else if (origin=='eqtl') {
		el_chk = document.getElementById("eqtlrow"+dbid);	
		if (el_chk!=null) {
			c_val = $("#eqtlrow"+dbid+" .ak_eqtl_comment").attr('data-curval');
		}	
	} else if (origin=='source') {
		el_chk = document.getElementById("sourcedocumentsrow"+dbid);
		if (el_chk!=null) {
			c_val = $("#sourcedocumentsrow"+dbid+" .ak_source_comment").attr('data-curval');
		}
	} else if (origin=='genephen') {
		el_chk = document.getElementById("genephenrow"+dbid);
		if (el_chk!=null) {
			c_val = $("#genephenrow"+dbid+" .ak_genephen_comment").attr('data-curval');
		}	
	}
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	if (origin=='detail' && newcomment=='') {
		$("#detailrow"+dbid).val(c_val);
		if ($("#detailrowsection"+dbid).length>0) {
			$("#detailrowsection"+dbid).val(c_val);
		}
		master_busy = false;
		return;
	}
	var whattodo = {
			todo: "updatecomment",
			origin: origin,
			comment: newcomment,
			db_id: dbid,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatecomment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp();
			var ele_chk = null;
			if (origin=='detail') {
				ele_chk = document.getElementById("detailrow"+dbid);
			} else if (origin=='gwas') {
				ele_chk = document.getElementById("gwasrow"+dbid);
			} else if (origin=='eqtl') {
				ele_chk = document.getElementById("eqtlrow"+dbid);
			} else if (origin=='source') {
				ele_chk = document.getElementById("sourcedocumentsrow"+dbid);
			} else if (origin=='genephen') {
				ele_chk = document.getElementById("genephenrow"+dbid);
			}
			if (returnedbyserver.isok=="ok") {
				if (origin=='detail') {
					if (ele_chk!=null) {
						$("#detailrow"+dbid).attr('data-curval', newcomment);
					}
					if ($("#detailrowsection"+dbid).length>0) {
						$("#detailrowsection"+dbid).attr('data-curval', newcomment);
					}
					if (fromwhere==1) {
						if ($("#detailrowsection"+dbid).length>0) {
							$("#detailrowsection"+dbid).val(newcomment);
						}
					} else {
						if (ele_chk!=null) {$("#detailrow"+dbid).val(newcomment);}
					}
				} else if (ele_chk!=null) {
					if (origin=='gwas') {
						$("#gwasrow"+dbid+" .ak_gwas_comment").attr('data-curval', newcomment);
					} else if (origin=='eqtl') {
						$("#eqtlrow"+dbid+" .ak_eqtl_comment").attr('data-curval', newcomment);
					} else if (origin=='source') {
						$("#sourcedocumentsrow"+dbid+" .ak_source_comment").attr('data-curval', newcomment);
					} else if (origin=='genephen') {
						$("#genephenrow"+dbid+" .ak_genephen_comment").attr('data-curval', newcomment);
					}
				}
				master_busy = false;
			} else {
				if (origin=='detail') {
					if (ele_chk!=null) {
						$("#detailrow"+dbid).val(c_val);
					}
					if ($("#detailrowsection"+dbid).length>0) {
						$("#detailrowsection"+dbid).val(c_val);
					}
				} else if (ele_chk!=null) {	
					if (origin=='gwas') {
						$("#gwasrow"+dbid+" .ak_gwas_comment").val(c_val);
					} else if (origin=='eqtl') {
						$("#eqtlrow"+dbid+" .ak_eqtl_comment").val(c_val);
					} else if (origin=='source') {
						$("#sourcedocumentsrow"+dbid+" .ak_source_comment").val(c_val);
					} else if (origin=='genephen') {
						$("#genephendocumentsrow"+dbid+" .ak_genephen_comment").val(c_val);
					}
				}
				console.log('updatecomment not successful at '+stamp);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before changing comments.");
				} else {
					alert("Problem saving comment to db");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			var ele_chk = null;
			if (origin=='detail') {
				ele_chk = document.getElementById("detailrow"+dbid);
			} else if (origin=='gwas') {
				ele_chk = document.getElementById("gwasrow"+dbid);
			} else if (origin=='eqtl') {
				ele_chk = document.getElementById("eqtlrow"+dbid);
			} else if (origin=='source') {
				ele_chk = document.getElementById("sourcedocumentsrow"+dbid);
			} else if (origin=='genephen') {
				ele_chk = document.getElementById("genephenrow"+dbid);
			}
			if (origin=='detail') {
				if (ele_chk!=null) {
					$("#detailrow"+dbid).val(c_val);
				}
				if ($("#detailrowsection"+dbid).length>0) {
					$("#detailrowsection"+dbid).val(c_val);
				}
			} else if (ele_chk!=null) {	
				if (origin=='gwas') {
					$("#gwasrow"+dbid+" .ak_gwas_comment").val(c_val);
				} else if (origin=='eqtl') {
					$("#eqtlrow"+dbid+" .ak_eqtl_comment").val(c_val);
				} else if (origin=='source') {
					$("#sourcedocumentsrow"+dbid+" .ak_source_comment").val(c_val);
				} else if (origin=='genephen') {
					$("#genephendocumentsrow"+dbid+" .ak_genephen_comment").val(c_val);
				}
			}
			console.log('updatecomment not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving comment to db");
		}
	});
};

updatesvgdisplayname = function(dbid, origin, svgdisplayname) {
	var index = -1;
	for (var i=0; i<sdn_timercheckids[origin].length; ++i) {
		if (sdn_timercheckids[origin][i]==dbid) {
			index = i;
			break;
		}
	}
	if (index==-1) {
		return; // should not happen
	}
	if (master_busy) {
		clearTimeout(sdn_timerchecks[origin][index]);
		sdn_timerchecks[origin][index] = setTimeout(function() {updatesvgdisplayname(dbid, origin, svgdisplayname)}, 2000);
		return;
	}
	master_busy = true;
	sdn_timerchecks[origin].splice(index,1);
	sdn_timercheckids[origin].splice(index,1);

	var el_chk = document.getElementById(origin+"row"+dbid);
	if (el_chk==null) {  // row got deleted somehow, weird, but we won't error because of it
		master_busy = false;
		return;
	}
	var c_val = $("#"+origin+"row"+dbid+" .ak_sdn").attr('data-curval');
	if (svgdisplayname=='') {
		$("#"+origin+"row"+dbid+" .ak_sdn").val(c_val);
		master_busy = false;
		return;
	}
	var whattodo = {
			todo: "updatesvgdisplayname",
			origin: origin,
			svgdisplayname: svgdisplayname,
			db_id: dbid,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatesvgdisplayname/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok=="ok") {
				var ele_chk = document.getElementById(origin+"row"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow
					$("#"+origin+"row"+dbid+" .ak_sdn").attr('data-curval', svgdisplayname);
				}
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					if (shown_supl_id=='svg'+origin+'_'+dbid) {
						var supl = document.getElementById('svg'+origin+'_'+dbid).getAttribute("data-supl");
						fill_supl_div(supl, 'svg'+origin+'_'+dbid);
					}
				}
				master_busy = false;
			} else {
				var ele_chk = document.getElementById(origin+"row"+dbid);
				if (ele_chk!=null) {  // row got deleted somehow
					$("#"+origin+"row"+dbid+" .ak_sdn").val(c_val);
				}
				console.log('updatesvgdisplayname not successful at '+stamp);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before changing display name.");
				} else {
					alert("Problem saving display name to db");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp()
			var ele_chk = document.getElementById(origin+"row"+dbid);
			if (ele_chk!=null) {  // row got deleted somehow
				$("#"+origin+"row"+dbid+" .ak_sdn").val(c_val);
			}
			console.log('updatesvgdisplayname not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem saving display name to db");
		}
	});
};

function AddNewEQTLOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "get_pub_and_gene_list",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_pub_and_gene_list/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				var pobj = JSON.parse(returnedbyserver.pub_list);
				var gobj = JSON.parse(returnedbyserver.gene_list);
				var mydiv = document.getElementById("addneweqtloverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="akneweqtldiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">New eQTL</p>';
				dcontent += '<p style="color:#000;">Source:';
				var s_html = '<select style="width:400px" class="ak_new_eqtl_overlay ak_new_eqtl_source_selector" >';
				s_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
				for (var i=0; i<pobj.length; ++i) {
					s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].column_display_text)+'</option>';
				}
				s_html += '</select>';
				dcontent += s_html+'</p>';
				dcontent += '<p style="color:#000;">Tissue: <input class="ak_new_eqtl_overlay" id="new_eqtl_tissue" style="text-align:center;width:250px;color:#000;" placeholder="Eg. Spleen" type="text" maxlength="150"></p>';
				dcontent += '<p style="color:#000;">Gene:';
				var g_html = '<select style="width:300px" class="ak_new_eqtl_overlay ak_new_eqtl_gene_selector" >';
				g_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
				for (var i=0; i<gobj.length; ++i) {
					g_html += '<option value="'+escapeHtml(gobj[i].gene_symbol)+'">'+escapeHtml(gobj[i].gene_symbol)+'</option>';
				}
				g_html += '</select>';
				dcontent += g_html+'</p>';
				dcontent += '<p style="color:#000;">Index variant: <input class="ak_new_eqtl_overlay" id="new_eqtl_indexvariant" style="text-align:center;width:105px;color:#000;" placeholder="Eg. rs123" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Pvalue: <input class="ak_new_eqtl_overlay" id="new_eqtl_pvalue" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 1.0e-12" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Beta: <input class="ak_new_eqtl_overlay" id="new_eqtl_beta" style="text-align:center;width:105px;color:#000;" placeholder="Eg. -0.808" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Effect allele (optional): <input class="ak_new_eqtl_overlay" id="new_eqtl_allele" style="text-align:center;width:105px;color:#000;" placeholder="Eg. T" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000; vertical-align: middle;">Comment: <textarea id="new_eqtl_comment" rows="4" cols="50" style="color:#000; vertical-align: middle;" placeholder="Enter comment" class="ak_new_eqtl_overlay ak_tablecell" type="text"></textarea></p>';
				dcontent += '<button id="ak_add_new_eqtl_button" style="margin-bottom:10px;" type="button" class="ak_new_eqtl_overlay btn btn-primary btn-sm" onclick="SaveNewEQTL()">Save</button>';
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_add_new_eqtl_x_button');
				tmpb.setAttribute('onclick', 'RemoveAddNewEQTLOverlay()');
				tmpb.setAttribute('class', 'ak_new_eqtl_overlay btn btn-primary btn-sm');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				$(".ak_new_eqtl_source_selector").select2();
				$(".ak_new_eqtl_gene_selector").select2();
				mydiv.style.display = 'block';
				master_busy = false;
			} else {
				console.log('problem recovering references and genes');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before adding an eQTL.");
				} else {
					alert("Problem recovering references and genes");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering references and genes');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering references and genes");
		}
	});
};

RemoveAddNewEQTLOverlay = function() {
	var mydiv = document.getElementById("addneweqtloverlay");
	mydiv.style.display = 'none';
}

SaveNewEQTL = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_new_eqtl_overlay').attr('disabled', true);
	var new_tissue = document.getElementById("new_eqtl_tissue").value;
	var new_indexvariant = document.getElementById("new_eqtl_indexvariant").value;
	var new_pvalue = document.getElementById("new_eqtl_pvalue").value;
	var new_beta = document.getElementById("new_eqtl_beta").value;
	var new_allele = document.getElementById("new_eqtl_allele").value;
	var source_selector = document.getElementsByClassName("ak_new_eqtl_source_selector")[0];
	var source_id = -1;
	if (source_selector.selectedIndex>-1) {
		source_id = source_selector.options[source_selector.selectedIndex].value;
	}
	var gene_selector = document.getElementsByClassName("ak_new_eqtl_gene_selector")[0];
	var gene = "";
	if (gene_selector.selectedIndex>-1) {
		gene = gene_selector.options[gene_selector.selectedIndex].value;
	}
	var comment_text = document.getElementById("new_eqtl_comment").value;
	var whattodo = {
			todo: "saveneweqtl",
			db_source_id: source_id,
			gene_symbol: gene,
			tissue: new_tissue,
			indexvariant: new_indexvariant,
			pvalue: new_pvalue,
			beta: new_beta,
			allele: new_allele,
			comment: comment_text,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/saveneweqtl/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('saveneweqtl not successful at '+stamp);
				$('.ak_new_eqtl_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving eQTL.");
				} else {
					alert("Problem saving eQTL to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('saveneweqtl had user errors at '+stamp);
					$('.ak_new_eqtl_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $row = $(returnedbyserver.newrow);
					$('#eqtltable').find('tbody').append($row).trigger('addRows', [$row]);
					$("#eqtlrow"+returnedbyserver.eqtldbid+" .ak_cs_selector").select2({minimumResultsForSearch: 20});
					$("#eqtlrow"+returnedbyserver.eqtldbid+" .ak_eqtl_comment").attr('data-curval', $("#eqtlrow"+returnedbyserver.eqtldbid+" .ak_eqtl_comment").val());
					$("#eqtlrow"+returnedbyserver.eqtldbid+" .ak_eqtl_sdn").attr('data-curval', $("#eqtlrow"+returnedbyserver.eqtldbid+" .ak_eqtl_sdn").val());
					$('.ak_new_eqtl_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addneweqtloverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('saveneweqtl not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_new_eqtl_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving new eQTL to db");
		}
	});
}

reflow_supl_div = function(id1, id2) {
	if (shown_supl_id==id1) {
		var svge = document.getElementById(id1);
		if (svge!=null) {
			var supl = svge.getAttribute("data-supl");
			fill_supl_div(supl, id1, '1');
		} else {
			clear_supl_div();
		}
	}
	if (shown_supl_id.indexOf(id2+'-')>-1) {
		var svge = document.getElementById(id2);
		if (svge!=null) {
			var supl = svge.getAttribute("data-supl");
			var classname = svge.getAttribute("data-class");
			var ldgids = svge.getAttribute("data-ldgids");
			fill_supl_div(supl, ldgids, '3', classname);
		} else {
			if (shown_supl_id==id2+'-') {clear_supl_div();}
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
		var svge = document.getElementById(id2);
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
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updateeqtlshowhide/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svgeqtl_'+db_id, 'ER'+db_id);
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('updateeqtlshowhide not successful at '+stamp);
				if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before showing/hiding result.");
				} else {
					alert("Problem showing/hiding eQTL result");
				}
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
			source_db_id: db_id,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatesourcereview/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				if (r_or_n=='Not Reviewed') {cbox.checked = true;} else {cbox.checked = false;}
				console.log('updatesourcereview not successful at '+stamp);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before further use.");
				} else {
					alert("Problem saving review update to db");
				}
			} else {
				master_busy = false;
				var rh3 = document.getElementById("referencesh3");
				rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
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
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/removeeqtlrow/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok=="ok") {
				$("#eqtlrow"+whichdbid).remove();
				$("#eqtltable").trigger('update');
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
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
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before removing eQTL entry.");
				} else {
					alert("Problem removing eQTL entry from db");
				}
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

CreateCustomCredibleSetOverlay = function(selector,gwas_or_eqtl) {
	var db_id = selector.getAttribute("data-dbid");
	var tr = '';
	var rsn = '';
	if (gwas_or_eqtl=='gwas') {
		tr = document.getElementById("gwasrow"+db_id);
		rsn = tr.cells[4].innerText;
	} else {
		tr = document.getElementById("eqtlrow"+db_id);
		rsn = tr.cells[5].innerText;		
	}
	var mydiv = document.getElementById("customcrediblesetoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="aknewcrediblesetdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
	dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">Custom Credible Set</p>';

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
	dcontent += '<button type="button" style="margin-bottom:10px;" class="ak_credibleset_overlay ak_table_button" onclick="AddCustomCredibleSetOverlayTableRow()">Add Row</button>';
	dcontent += '<button id="ak_create_credibleset_button" style=margin-bottom:10px;" type="button" class="ak_credibleset_overlay btn btn-primary btn-sm" onclick="SaveCustomCredibleSet('+db_id+',\''+gwas_or_eqtl+'\')">Create Credible Set</button>';
	dcontent += "</div>";
	mydiv.innerHTML = dcontent;
	var tmpb = document.createElement('button');
	tmpb.setAttribute('id', 'ak_credibleset_x_button');
	tmpb.setAttribute('onclick', 'RemoveCustomCredibleSetOverlay()');
	tmpb.setAttribute('class', 'ak_credibleset_overlay btn btn-primary btn-sm');
	tmpb.style.verticalAlign='top';
	tmpb.style.display='inline-block';
	tmpb.innerHTML = 'X';
	mydiv.appendChild(tmpb);
	$('#akcrediblesettable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['50%','50%']}});
	mydiv.style.display = 'block';
};

AddCustomCredibleSetOverlayTableRow = function(selector) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var $row = $('<tr><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. rs123" type="text" maxlength="15"></td><td><input class="ak_credibleset_overlay" style="text-align:center;width:100%" placeholder="Eg. 0.123" type="text" maxlength="15"></td></tr>');
	$("#akcrediblesettable").find('tbody').append($row).trigger('addRows', [$row]);
	master_busy = false;
}

RemoveCustomCredibleSetOverlay = function() {
	var mydiv = document.getElementById("customcrediblesetoverlay");
	mydiv.style.display = 'none';
}

SaveCustomCredibleSet = function(db_id, gwas_or_eqtl) {
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
			todo: "createcustomcredibleset",
			gwas_or_eqtl: gwas_or_eqtl,
			db_id: db_id,
			credible_set_name: cs_name,
			index_variant_posterior: cs_index_posterior,
			member_rs_numbers: cs_rs_list,
			member_posteriors: cs_posteriors,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/createcustomcredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('createcustomcredibleset not successful at '+stamp);
				$('.ak_credibleset_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before creating credible set.");
				} else {
					alert("Problem saving credible set to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('createcustomcredibleset had user errors at '+stamp);
					$('.ak_credibleset_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					if (gwas_or_eqtl=='gwas') {
						$('#gwasrow'+db_id+' .ak_cs_selector option[value="Add custom"]').detach();
						var newOption1 = new Option(returnedbyserver.csname, 'Custom', false, false);
						var newOption2 = new Option('Delete custom', 'Delete custom', false, false);
						updatecsresetting = 1;
						$('#gwasrow'+db_id+' .ak_cs_selector').append(newOption1).trigger('change');
						$('#gwasrow'+db_id+' .ak_cs_selector').append(newOption2).trigger('change');
					} else {
						$('#eqtlrow'+db_id+' .ak_cs_selector option[value="Add custom"]').detach();
						var newOption1 = new Option(returnedbyserver.csname, 'Custom', false, false);
						var newOption2 = new Option('Delete custom', 'Delete custom', false, false);
						updatecsresetting = 1;
						$('#eqtlrow'+db_id+' .ak_cs_selector').append(newOption1).trigger('change');
						$('#eqtlrow'+db_id+' .ak_cs_selector').append(newOption2).trigger('change');
					}
					updatecsresetting = 0;
					$('.ak_credibleset_overlay').attr('disabled', false);
					var mydiv = document.getElementById("customcrediblesetoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('createcustomcredibleset not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_credibleset_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving credible set to db");
		}
	});
}

DeleteGWASCustomCredibleSet = function(selector) {
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-ccs");
	var whattodo = {
			todo: "deletegwascustomcredibleset",
			gwas_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/deletegwascustomcredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				var eqtllist = JSON.parse(returnedbyserver.eqtl_ids);
				var overlap_counts = JSON.parse(returnedbyserver.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svggwas_'+db_id,'GR'+db_id);
				}
				$('#gwasrow'+db_id+' .ak_cs_selector option[value="Delete custom"]').detach();
				$('#gwasrow'+db_id+' .ak_cs_selector option[value="Custom"]').detach();
				var newOption1 = new Option('Add custom', 'Add custom', false, false);
				$('#gwasrow'+db_id+' .ak_cs_selector').append(newOption1).trigger('change');
				if (c_val=='Custom') {
					selector.setAttribute("data-ccs", 'Unset');
					$(selector).val('Unset').trigger('change');
				}
				updatecsresetting=0;
				master_busy = false;
			} else {
				var stamp = aktimeStamp();
				console.log('deletegwascustomcredibleset not successful at '+stamp);
				updatecsresetting=0;
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before deleting credible set.");
				} else {
					alert("Problem deleting GWAS custom credible set from db");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('deletegwascustomcredibleset not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			updatecsresetting=0;
			master_busy = false;
			alert("Problem deleting GWAS custom credible set from db");
		}
	});
};

DeleteEQTLCustomCredibleSet = function(selector) {
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-ccs");
	var whattodo = {
			todo: "deleteeqtlcustomcredibleset",
			eqtl_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/deleteeqtlcustomcredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svgeqtl_'+db_id,'ER'+db_id);
				}
				$('#eqtlrow'+db_id+' .ak_cs_selector option[value="Delete custom"]').detach();
				$('#eqtlrow'+db_id+' .ak_cs_selector option[value="Custom"]').detach();
				var newOption1 = new Option('Add custom', 'Add custom', false, false);
				$('#eqtlrow'+db_id+' .ak_cs_selector').append(newOption1).trigger('change');
				if (c_val=='Custom') {
					selector.setAttribute("data-ccs", 'Unset');
					$(selector).val('Unset').trigger('change');
				}
				updatecsresetting=0;
				master_busy = false;
			} else {
				var stamp = aktimeStamp();
				console.log('deleteeqtlcustomcredibleset not successful at '+stamp);
				updatecsresetting=0;
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before deleting credible set.");
				} else {
					alert("Problem deleting eQTL custom credible set from db");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('deleteeqtlcustomcredibleset not successful at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			updatecsresetting=0;
			master_busy = false;
			alert("Problem deleting eQTL custom credible set from db");
		}
	});
};


function ShowAboutOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var mydiv = document.getElementById("aboutoverlay");
	mydiv.innerHTML = '';
	var dcontent = '<div id="akaboutdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 14px; text-align: center; color:#000;">';
	$.ajax({
		url: "content/about.txt",
		type: "get",
		timeout: 30000,
		dataType: "text",
		success: function(returnedbyserver){
			dcontent+=returnedbyserver;
			dcontent += "</div>";
			mydiv.innerHTML = dcontent;
			var tmpb = document.createElement('button');
			tmpb.setAttribute('id', 'ak_about_x_button');
			tmpb.setAttribute('onclick', 'RemoveAboutOverlay()');
			tmpb.setAttribute('class', 'ak_about_overlay btn btn-primary btn-sm');
			tmpb.style.verticalAlign='top';
			tmpb.style.display='inline-block';
			tmpb.innerHTML = 'X';
			mydiv.appendChild(tmpb);
			mydiv.style.display = 'block';
			master_busy = false;
		},
		error: function(xhr, textStatus, errorThrown){
			master_busy = false;
			alert("Problem reporting About information");
		}
	});
};

RemoveAboutOverlay = function() {
	var mydiv = document.getElementById("aboutoverlay");
	mydiv.style.display = 'none';
}

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
	dcontent += '<p style="color:#000; vertical-align: middle;"><a href="javascript:;" class="ak_detail_overlay" id="ak_paste_a" ><div id="ak_dandd" style="width: 300px; height: 200px; display: inline-block; vertical-align: top;"><img id="ak_paste_img" class="ak_detail_overlay contain_img" width="300" height="200"></img></div></a></p>';
	dcontent += '<button id="ak_add_detail_button" style="margin-bottom:10px;" type="button" class="ak_detail_overlay btn btn-primary btn-sm" onclick="SaveNewDetail('+row_id+')">Save</button>';
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
			img_b64: img_b64_text,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitnewdetail/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitnewdetail not successful at '+stamp);
				$('.ak_detail_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving Detail.");
				} else {
					alert("Problem saving new Detail to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('submitnewdetail had user errors at '+stamp);
					$('.ak_detail_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					$("#adddetail"+sd_id).replaceWith(returnedbyserver.trtext);
					$("#reftable").trigger('update');
					var rs = document.getElementById("sourcedocumentsrow"+sd_id).getElementsByTagName("td")[7].getAttribute('rowspan');
					rs++;
					document.getElementById("sourcedocumentsrow"+sd_id).getElementsByTagName("td")[7].setAttribute('rowspan', rs);
					var but = document.getElementById("sourcedocumentsrow"+sd_id).getElementsByClassName("detail-toggle")[0];
					but.firstChild.data = 'Hide Details ('+(rs-2)+')';
					allow_image_paste = false;
					$("#detail"+returnedbyserver.detaildbid+" .ak_detail_section_selector").select2({minimumResultsForSearch: 20});
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
			detail_id: d_id,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/removedetail/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('removedetail not successful at '+stamp);
				$('.akdetailrowclass'+d_id).attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before deleting detail.");
				} else {
					alert("Problem deleting detail from db");
				}
			} else {
				if (returnedbyserver.old_section_lc!=="None") {
					//$("#"+returnedbyserver.old_section_lc+"table").trigger('disablePager');
					$("#"+returnedbyserver.old_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
					$("#"+returnedbyserver.old_section_source_documents_id).replaceWith(returnedbyserver.old_section_revised_rows);
					$("#"+returnedbyserver.old_section_lc+"table").trigger('update');
					//$("#"+returnedbyserver.old_section_lc+"table").trigger('enablePager');
					$("#"+returnedbyserver.old_section_lc+"table").find('tr').show(); // not sure why needed
				}
				//$("#reftable").trigger('disablePager');
				$("#"+detailid).remove();
				var rs = document.getElementById("sourcedocumentsrow"+returnedbyserver.source_documents_id).getElementsByTagName("td")[7].getAttribute('rowspan');
				rs--;
				document.getElementById("sourcedocumentsrow"+returnedbyserver.source_documents_id).getElementsByTagName("td")[7].setAttribute('rowspan', rs);
				var but = document.getElementById("sourcedocumentsrow"+returnedbyserver.source_documents_id).getElementsByClassName("detail-toggle")[0];
				if (rs > 2) {
					but.firstChild.data = 'Hide Details ('+(rs-2)+')';
				} else {
					but.firstChild.data = 'Hide Details';
				}
				//$("#reftable").trigger('enablePager');
				$("#reftable").trigger('update');
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
			detail_id: cl_id,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitdetailsectionassignment/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitdetailsectionassignment not successful at '+stamp);
				updatesectionresetting=1;
				$(selector).val(c_val).trigger('change');
				updatesectionresetting=0;
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before assigning to section.");
				} else {
					alert("Problem saving new section assignment to db");
				}
			} else {
				selector.setAttribute("data-csection", n_val);
				if (returnedbyserver.old_section_lc!=="none") {
					//$("#"+returnedbyserver.old_section_lc+"detailstable").trigger('disablePager');
					$("#"+returnedbyserver.old_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
					$("#"+returnedbyserver.old_section_source_documents_id).replaceWith(returnedbyserver.old_section_revised_rows);
					$("#"+returnedbyserver.old_section_lc+"detailstable").trigger('update');
					//$("#"+returnedbyserver.old_section_lc+"detailstable").trigger('enablePager');
					$("#"+returnedbyserver.old_section_lc+"detailstable").find('tr').show(); // not sure why needed
				}
				if (returnedbyserver.new_section_lc!=="none") {
					//$("#"+returnedbyserver.new_section_lc+"detailstable").trigger('disablePager');
					if ( $("#"+returnedbyserver.new_section_source_documents_id).length ) {
						$("#"+returnedbyserver.new_section_source_documents_id).nextUntil(':not(.tablesorter-childRow)').remove();
						$("#"+returnedbyserver.new_section_source_documents_id).replaceWith(returnedbyserver.new_section_revised_rows);
					} else {
						var $rows = $(returnedbyserver.new_section_revised_rows);
						$("#"+returnedbyserver.new_section_lc+"detailstable").find('tbody').append($rows);//.trigger('addRows', [$rows]);
					}
					$("#"+returnedbyserver.new_section_lc+"detailstable").trigger('update');
					//$("#"+returnedbyserver.new_section_lc+"detailstable").trigger('enablePager');
					$("#"+returnedbyserver.new_section_lc+"detailstable").find('tr').show(); // not sure why needed
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
	dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">New Pubmed</p>';
	dcontent += '<p style="color:#000;">Pubmed ID: <input class="ak_pubmed_overlay" id="new_pubmed_id" style="text-align:center;width:250px;color:#000;" placeholder="Eg. 23817569" type="text" maxlength="15"></p>';
	dcontent += '<button id="ak_add_pubmed_button" style="margin-bottom:10px;" type="button" class="ak_pubmed_overlay btn btn-primary btn-sm" onclick="SaveNewPubmed()">Save</button>';
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
			pubmed_id: pubmed_id_text,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitnewpubmed/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitnewpubmed not successful at '+stamp);
				$('.ak_pubmed_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving Pubmed reference.");
				} else {
					alert("Problem saving Pubmed to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('submitnewpubmed had user errors at '+stamp);
					$('.ak_pubmed_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $trs = $(returnedbyserver.trtext);
					$("#reftable").find('tbody').append($trs).trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).replaceWith(returnedbyserver.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_pubmed_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addpubmedoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
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
	dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">New bioRxiv</p>';
	dcontent += '<p style="color:#000;">bioRxiv ID: <input class="ak_biorxiv_overlay" id="new_biorxiv_id" style="text-align:center;width:250px;color:#000;" placeholder="Eg. 10.1101/045831" type="text" maxlength="30"></p>';
	dcontent += '<button id="ak_add_biorxiv_button" style="margin-bottom:10px;" type="button" class="ak_biorxiv_overlay btn btn-primary btn-sm" onclick="SaveNewBiorxiv()">Save</button>';
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
			doi: doi_text,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitnewbiorxiv/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitnewbiorxiv not successful at '+stamp);
				$('.ak_biorxiv_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving bioRxiv reference.");
				} else {
					alert("Problem saving bioRxiv to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('submitnewbiorxiv had user errors at '+stamp);
					$('.ak_biorxiv_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $trs = $(returnedbyserver.trtext);
					$("#reftable").find('tbody').append($trs).trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).replaceWith(returnedbyserver.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_biorxiv_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addbiorxivoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
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
	dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">New File</p>';
	dcontent += '<p style="color:#000;">Year: <input class="ak_file_overlay" id="new_file_year" style="text-align:center;width:125px;color:#000;" placeholder="Eg. 2018" type="text" maxlength="4"></p>';
	dcontent += '<p style="color:#000;"><input id="new_file_file" class="ak_file_overlay" style="text-align:center;width:250px" type="file" onchange="RecoverB64RefFile(this)"></p>';
	dcontent += '<p style="color:#000; vertical-align: middle;"><textarea id="new_file_description" rows="4" cols="48" style="color:#000; vertical-align: middle;" placeholder="Enter description" class="ak_file_overlay ak_tablecell" type="text"></textarea></p>';
	dcontent += '<button id="ak_add_file_button" style="margin-bottom:10px;" type="button" class="ak_file_overlay btn btn-primary btn-sm" onclick="SaveNewFile()">Save</button>';
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
			file_contents: b64contents,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitnewfile/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitnewfile not successful at '+stamp);
				$('.ak_file_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving file reference.");
				} else {
					alert("Problem saving file to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('submitnewfile had user errors at '+stamp);
					$('.ak_file_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $trs = $(returnedbyserver.trtext);
					$("#reftable").find('tbody').append($trs).trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).replaceWith(returnedbyserver.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_file_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addfileoverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
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
	dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">New Web</p>';
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
	dcontent += '<button id="ak_add_web_button" style="margin-bottom:10px;" type="button" class="ak_web_overlay btn btn-primary btn-sm" onclick="SaveNewWeb()">Save</button>';
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
			web_description: new_web_description,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/submitnewweb/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('submitnewweb not successful at '+stamp);
				$('.ak_web_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving site reference.");
				} else {
					alert("Problem saving site to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('submitnewweb had user errors at '+stamp);
					$('.ak_web_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $trs = $(returnedbyserver.trtext);
					$("#reftable").find('tbody').append($trs).trigger('addRows', [$trs, true]);
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).replaceWith(returnedbyserver.trtext2);
					$("#reftable").trigger('update');
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid).next('tr').find('td').hide();
					$("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").attr('data-curval', $("#sourcedocumentsrow"+returnedbyserver.sourcedocumentsid+" .ak_source_comment").val());
					$('.ak_web_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addweboverlay");
					mydiv.style.display = 'none';
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
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
			sd_db_id: row_id,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/oktoremoveref/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok=="ok") {
				if (returnedbyserver.problems=="") {
					//$("#reftable").trigger('disablePager');
					if (document.getElementById("adddetail"+row_id)) {
						$("#adddetail"+row_id).remove();
					}
					$("#"+whichdbid).remove();
					$("#reftable").trigger('update');
					//$("#reftable").trigger('enablePager');
					var rh3 = document.getElementById("referencesh3");
					rh3.childNodes[1].textContent = 'References ('+returnedbyserver.unreviewed_count+' out of '+returnedbyserver.total_count+' not yet reviewed)';
				} else {
					$('.akrefrowclass'+row_id).attr('disabled', false);
					alert(returnedbyserver.problems);
				}
				master_busy = false;
			} else {
				console.log('problem removing reference at '+stamp);
				$('.akrefrowclass'+row_id).attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before removing reference.");
				} else {
					alert("Problem removing reference");
				}
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

function AddNewGWASOverlay(ispqtl) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	
	var whattodo = {
			todo: "get_pub_list",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_pub_list/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				var pobj = JSON.parse(returnedbyserver.pub_list);
				var mydiv = document.getElementById("addnewgwasoverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="aknewgwasdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				if (ispqtl==0) {
					dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">New Association</p>';
				} else {
					dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">New pQTL</p>';
				}
				dcontent += '<p style="color:#000;">Phenotype: <input class="ak_new_gwas_overlay" id="new_gwas_phenotype" style="text-align:center;width:250px;color:#000;" placeholder="Eg. Schizophrenia" type="text" maxlength="150"></p>';
				dcontent += '<p style="color:#000;">Source:';
				var s_html = '<select style="width:400px" class="ak_new_gwas_overlay ak_new_gwas_source_selector" >';
				s_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
				for (var i=0; i<pobj.length; ++i) {
					s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].column_display_text)+'</option>';
				}
				s_html += '</select>';
				dcontent += s_html+'</p>';
				dcontent += '<p style="color:#000;">Index variant: <input class="ak_new_gwas_overlay" id="new_gwas_indexvariant" style="text-align:center;width:105px;color:#000;" placeholder="Eg. rs123" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Allele (optional): <input class="ak_new_gwas_overlay" id="new_gwas_allele" style="text-align:center;width:105px;color:#000;" placeholder="Eg. T" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Pvalue: <input class="ak_new_gwas_overlay" id="new_gwas_pvalue" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 3.0e-13" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">OR/Beta: <input class="ak_new_gwas_overlay" id="new_gwas_orbeta" style="text-align:center;width:105px;color:#000;" placeholder="Eg. 1.11" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000; vertical-align: middle;">Comment: <textarea id="new_gwas_comment" rows="4" cols="50" style="color:#000; vertical-align: middle;" placeholder="Enter comment" class="ak_new_gwas_overlay ak_tablecell" type="text"></textarea></p>';
				dcontent += '<button id="ak_add_new_gwas_button" style="margin-bottom:10px;" type="button" class="ak_new_gwas_overlay btn btn-primary btn-sm" onclick="SaveNewGWAS('+ispqtl+')">Save</button>';
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_add_new_gwas_x_button');
				tmpb.setAttribute('onclick', 'RemoveAddNewGWASOverlay()');
				tmpb.setAttribute('class', 'ak_new_gwas_overlay btn btn-primary btn-sm');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				$(".ak_new_gwas_source_selector").select2();
				mydiv.style.display = 'block';
				master_busy = false;
			} else {
				console.log('problem recovering references');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before adding a GWAS/pQTL.");
				} else {
					alert("Problem recovering references");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering references');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering references");
		}
	});
		
};

RemoveAddNewGWASOverlay = function() {
	var mydiv = document.getElementById("addnewgwasoverlay");
	mydiv.style.display = 'none';
}

SaveNewGWAS = function(ispqtl) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_new_gwas_overlay').attr('disabled', true);
	var therow = document.getElementById("new_gwas_row");
	var pheno_text = document.getElementById("new_gwas_phenotype").value;
	var index_variant_text = document.getElementById("new_gwas_indexvariant").value;
	var pvalue_text = document.getElementById("new_gwas_pvalue").value;
	var or_beta_text = document.getElementById("new_gwas_orbeta").value;
	var allele_text = document.getElementById("new_gwas_allele").value;
	var source_selector = document.getElementsByClassName("ak_new_gwas_source_selector")[0];
	var source_id = -1;
	if (source_selector.selectedIndex>-1) {
		source_id = source_selector.options[source_selector.selectedIndex].value;
	}
	var comment_text = document.getElementById("new_gwas_comment").value;
	var whattodo = {
			todo: "savenewgwas",
			phenotype: pheno_text,
			source_db_id: source_id,
			index_variant: index_variant_text,
			allele: allele_text,
			pvalue: pvalue_text,
			or_beta: or_beta_text,
			comment: comment_text,
			is_pqtl: ispqtl,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/savenewgwas/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok!="ok") {
				console.log('savenewgwas not successful at '+stamp);
				$('.ak_new_gwas_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before saving GWAS/pQTL.");
				} else {
					alert("Problem saving GWAS/pQTL to db");
				}
			} else {
				if (returnedbyserver.errors!="") {
					console.log('savenewgwas had user errors at '+stamp);
					$('.ak_new_gwas_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var $row = $(returnedbyserver.trtext);
					if (ispqtl==0) {
						$("#gwastable").find('tbody').append($row).trigger('addRows', [$row]);	
					} else {
						$("#pqtltable").find('tbody').append($row).trigger('addRows', [$row]);	
					}
					$("#gwasrow"+returnedbyserver.gwasdbid+" .ak_cs_selector").select2({minimumResultsForSearch: 20});
					$("#gwasrow"+returnedbyserver.gwasdbid+" .ak_gwas_comment").attr('data-curval', $("#gwasrow"+returnedbyserver.gwasdbid+" .ak_gwas_comment").val());
					$("#gwasrow"+returnedbyserver.gwasdbid+" .ak_gwas_sdn").attr('data-curval', $("#gwasrow"+returnedbyserver.gwasdbid+" .ak_gwas_sdn").val());
					$('.ak_new_gwas_overlay').attr('disabled', false);
					var mydiv = document.getElementById("addnewgwasoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('problem with savenewgwas at '+stamp);
			console.log(xhr, textStatus, errorThrown);
			$('.ak_new_gwas_overlay').attr('disabled', false);
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
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/removegwasrow/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok=="ok") {
				var eqtllist = JSON.parse(returnedbyserver.eqtl_ids);
				var overlap_counts = JSON.parse(returnedbyserver.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				$("#gwasrow"+whichdbid).remove();
				if (ispqtl==0) {
					//$("#gwastable").trigger('disablePager');
					$("#gwastable").trigger("update");
					//$("#gwastable").trigger('enablePager');
				} else {
					//$("#pqtltable").trigger('disablePager');
					$("#pqtltable").trigger("update");
					//$("#pqtltable").trigger('enablePager');
				}
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
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
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before removing GWAS/pQTL entry.");
				} else {
					alert("Problem removing GWAS/pQTL entry from db");
				}
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
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updategwasshowhide/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				var eqtllist = JSON.parse(returnedbyserver.eqtl_ids);
				var overlap_counts = JSON.parse(returnedbyserver.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svggwas_'+db_id,'GR'+db_id);
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('problem with updategwasshowhide at '+stamp);
				if (s_or_h=='Hide') {cbox.checked = true;} else {cbox.checked = false;}
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before showing/hiding result.");
				} else {
					alert("Problem showing/hiding GWAS/pQTL result");
				}
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
			svg_display_mode: SVGMode,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updateshowhidenoncoding/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
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
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before changing non-coding gene visibility.");
				} else {
					alert("Problem showing/hiding non-coding genes");
				}
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
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatemarkerforld/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
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
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before changing the LD-display inclusion.");
				} else {
					alert("Problem adding/removing marker from LD display");
				}
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

var updatecsresetting = 0; // not bulletproof since one variable used for all mes assignments, but prob better than nothing

UpdateGWASCredibleSet = function(selector) {
	if (updatecsresetting==1) {return false;}
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-ccs");
	var n_val = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		updatecsresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	if (n_val=='Add custom') {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		updatecsresetting=0;
		CreateCustomCredibleSetOverlay(selector,'gwas');
		master_busy = false;
		return false;
	}
	if (n_val=='Delete custom') {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		DeleteGWASCustomCredibleSet(selector);
		return false;
	}
	var whattodo = {
			todo: "updategwascredibleset",
			credible_set: n_val,
			gwas_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updategwascredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				var eqtllist = JSON.parse(returnedbyserver.eqtl_ids);
				var overlap_counts = JSON.parse(returnedbyserver.overlap_counts);
				for (var j=0; j<eqtllist.length; ++j) {
					var td = document.getElementById('eqtloverlap'+eqtllist[j]);
					if (td!=null) {
						td.innerHTML = overlap_counts[j];
					}
				}
				$("#eqtltable").trigger("update");
				selector.setAttribute("data-ccs", n_val);
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svggwas_'+db_id,'GR'+db_id);
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('updategwascredibleset not successful at '+stamp);
				updatecsresetting=1;
				$(selector).val(c_val).trigger('change');
				updatecsresetting=0;
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before updating credible set.");
				} else {
					alert("Problem updating GWAS Credible Set");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('updategwascredibleset not successful at '+stamp);
			updatecsresetting=1;
			$(selector).val(c_val).trigger('change');
			updatecsresetting=0;
			master_busy = false;
			alert("Problem updating GWAS Credible Set");
		}
	});
};

UpdateEQTLCredibleSet = function(selector) {
	if (updatecsresetting==1) {return false;}
	var db_id = selector.getAttribute("data-dbid");
	var c_val = selector.getAttribute("data-ccs");
	var n_val = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		updatecsresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	if (n_val=='Add custom') {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		updatecsresetting=0;
		CreateCustomCredibleSetOverlay(selector,'eqtl');
		master_busy = false;
		return false;
	}
	if (n_val=='Delete custom') {
		updatecsresetting=1;
		$(selector).val(c_val).trigger('change');
		DeleteEQTLCustomCredibleSet(selector);
		return false;
	}
	var whattodo = {
			todo: "updateeqtlcredibleset",
			credible_set: n_val,
			eqtl_db_id: db_id,
			svg_display_mode: SVGMode,
			hidenoncoding: hide_non_coding,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updateeqtlcredibleset/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				selector.setAttribute("data-ccs", n_val);
				if (returnedbyserver.sentcontents!="") {
					var mydiv = document.getElementById("paper1");
					mydiv.removeChild(mydiv.firstElementChild);
					var doc = new DOMParser().parseFromString(returnedbyserver.sentcontents, 'application/xml');
					mydiv.appendChild(mydiv.ownerDocument.importNode(doc.documentElement, true));
					svginit();
					reflow_supl_div('svgeqtl_'+db_id,'ER'+db_id);
				}
				master_busy = false;
			} else {
				var stamp = aktimeStamp()
				console.log('updateeqtlcredibleset not successful at '+stamp);
				updatecsresetting=1;
				$(selector).val(c_val).trigger('change');
				updatecsresetting=0;
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before updating credible set.");
				} else {
					alert("Problem updating eQTL Credible Set");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			var stamp = aktimeStamp();
			console.log('updateeqtlcredibleset not successful at '+stamp);
			updatecsresetting=1;
			$(selector).val(c_val).trigger('change');
			updatecsresetting=0;
			master_busy = false;
			alert("Problem updating eQTL Credible Set");
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
	// 1 = gwas/eqtl summary table, 3 = ld group summary table
	if (supltype=='1') {
		var num_cols = $('#aktmptable').find('tr')[0].cells.length;
		if (num_cols==3) {
			$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['40%','30%','30%']}});
		} else {
			$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['25%', '39%','18%','28%']}});			
		}
	} else if (supltype=='3') {
		$('#aktmptable').tablesorter({theme:'blue', widgets: ['resizable'], widgetOptions:{resizable_addLastColumn:false, resizable_widths:['11%','11%','9%','9%','13%', '14%', '15%', '9%','9%']}});
	
	}
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
			$('#group_for_cs').css('visibility', 'visible');
			$('#group_for_ld').css('visibility', 'hidden');
		} else {
			$('#group_for_cs').css('visibility', 'hidden');
			$('#group_for_ld').css('visibility', 'visible');
		}
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	if (SVGMode=='Credible Sets') {
		$('#group_for_cs').css('visibility', 'visible');
		$('#group_for_ld').css('visibility', 'hidden');
	} else {
		$('#group_for_cs').css('visibility', 'hidden');
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
			sd_db_id: row_id,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/downloadfile/"+encodeURIComponent(gene_symbol),
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			var stamp = aktimeStamp()
			if (returnedbyserver.isok=="ok") {
				var byteString = atob(returnedbyserver.b64.split(',')[1]);
				var mimeString = returnedbyserver.b64.split(',')[0].split(':')[1].split(';')[0];
				var ab = new ArrayBuffer(byteString.length);
				var ia = new Uint8Array(ab);
				for (var i = 0; i < byteString.length; i++) {
					ia[i] = byteString.charCodeAt(i);
				}
				var blob = new Blob([ab], {type: mimeString});
				saveAs(blob, returnedbyserver.name);
				master_busy = false;
			} else {
				console.log('download not successful at '+stamp);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else if (returnedbyserver.isok=='backend db mismatch') {
					alert("Version of target notebook does not match the server db version.  Please resolve this before downloading file.");
				} else {
					alert("Problem downloading file");
				}
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