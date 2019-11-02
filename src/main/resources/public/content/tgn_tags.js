"use strict;"

var all_set_tags = {};
var all_tgns = [];
var tag_ids_for_db_filtering = [];
var current_tag = '';
var current_tgn = '';
var current_tag_id = '';
var current_tag_color = '';
var current_tag_text_color = '';

function formatState(state) {
	if (!state.id) {return state.text;}
	var isrc = $('#tagid'+state.id+' svg');
	var serializer = new XMLSerializer();
	var str = serializer.serializeToString(isrc[0]);
	var $state = $(str);
	return $state;
}

function FilterTableRows() {
	var table = document.getElementById("available_notebooks");
	for (var i = 2; i < table.rows.length; i++) {
		if (table.rows[i].cells.length) {
			var rw = $(table.rows[i]);
			var make_visible = 1;
			for (var j=0; j<tag_ids_for_db_filtering.length; ++j) {
				var t_mv = 0;
				rw.find("svg").each(function (index, element) {
					if ($(element).hasClass("tagidclass"+tag_ids_for_db_filtering[j])) {
						t_mv=1;
					}
				});
				if (t_mv==0) {
					make_visible=0;
					break;
				}
			}
			if (make_visible==1) {
				table.rows[i].style.display = '';
			} else {
				table.rows[i].style.display = 'none';
			}
		}
	}
}

function AdjustDBFilter(button) {
	var c_val = button.getAttribute("data-is_selected");
	var t_id = button.getAttribute("data-tag_id");
	if (c_val==0) {
		button.style.filter = '';
		button.setAttribute('data-is_selected', '1');
		tag_ids_for_db_filtering.push(t_id);
	} else {
		button.style.filter = 'grayscale(100%)';
		button.setAttribute('data-is_selected', '0');
		for (var j=0; j<tag_ids_for_db_filtering.length; ++j) {
			if (tag_ids_for_db_filtering[j]==t_id) {tag_ids_for_db_filtering.splice(j, 1); break;}
		}
	}
	FilterTableRows();
}

var tagselectorresetting = 0;
function ChangedTagSelector(selector) {
	if (tagselectorresetting==1) {return false;}
	var c_val = selector.getAttribute("data-ctid");
	var n_tag_id = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		tagselectorresetting=1;
		$(selector).val(c_val).trigger('change');
		tagselectorresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	selector.setAttribute("data-ctid", n_tag_id);
	var myrow = document.getElementById("tagid"+n_tag_id);
	current_tag = myrow.getElementsByClassName("short_name")[0].innerHTML;
	current_tag_id = n_tag_id;
	current_tag_color = myrow.getAttribute('data-tag_color');
	current_tag_text_color = myrow.getAttribute('data-tag_text_color');
	for (var prop in all_set_tags) {
		if (all_set_tags.hasOwnProperty(prop)) {
			var assigned_selected_tag=0;
			for (var j=0; j<all_set_tags[prop].length; ++j) {
				if (all_set_tags[prop][j]==current_tag) {assigned_selected_tag=1;}
			}
			var but = document.getElementById("db_button_"+prop)
			if (assigned_selected_tag==0) {
				but.style.color = '';
				but.style.backgroundColor = '';
			} else {
				but.style.color = current_tag_text_color;
				but.style.backgroundColor = current_tag_color;
			}
			but.setAttribute('data-is_assigned_to_tag', assigned_selected_tag);
		}
	}
	master_busy = false;
}

function SelectAllDBs(select_if_1) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	for (var i=0; i<all_tgns.length; ++i) {
		var but = document.getElementById("pdb_button_"+all_tgns[i]);
		if (but!=null) {
			if (select_if_1==0) {
				but.style.backgroundColor = 'White';
			} else {
				but.style.backgroundColor = 'DodgerBlue';
			}
			but.setAttribute('data-is_assigned_to_push', select_if_1);
		}
	}
	master_busy = false;
}

function ToggleForWebPush(button) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	var c_val = button.getAttribute("data-is_assigned_to_push");
	c_val = 1 - c_val;
	if (c_val==0) {
		button.style.backgroundColor = 'White';
	} else {
		button.style.backgroundColor = 'DodgerBlue';
	}
	button.setAttribute('data-is_assigned_to_push', c_val);
	master_busy = false;
}

var tgnselectorresetting = 0;
function ChangedTGNSelector(selector) {
	if (tgnselectorresetting==1) {return false;}
	var c_val = selector.getAttribute("data-ctgn");
	var n_tgn = selector.options[selector.selectedIndex].value;
	if (master_busy) {
		tgnselectorresetting=1;
		$(selector).val(c_val).trigger('change');
		tgnselectorresetting=0;
		alert("Please re-try after background actions have completed.");
		return false;
	}
	master_busy = true;
	selector.setAttribute("data-ctgn", n_tgn);
	current_tgn = n_tgn;
	var alltagrows = document.getElementsByClassName("a_tag_row");
	for (var ij=0; ij<alltagrows.length; ++ij) {
		var assigned_selected_tag=0;
		var sname = alltagrows[ij].getElementsByClassName("short_name")[0].innerHTML;
		var tid = alltagrows[ij].getAttribute('id');
		tid = tid.replace("tagid", "");
		var but = document.getElementById("tag_button_"+tid)
		if (all_set_tags[current_tgn].indexOf(sname)>-1) {
			but.style.filter = '';
			but.setAttribute('data-is_assigned_to_tgn', '1');
		} else {
			but.style.filter = 'grayscale(100%)';
			but.setAttribute('data-is_assigned_to_tgn', '0');
		}
	}
	master_busy = false;
}

ToggleAssignToCurrentTGN = function(button) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var is_currently_assigned = button.getAttribute('data-is_assigned_to_tgn');
	var tag_short = button.getAttribute('data-shortname');
	var to_assign = 1 - is_currently_assigned;
	var tag_id = button.id;
	tag_id = tag_id.replace("tag_button_", "");
	var whattodo = {
			todo: "updatetagassign",
			tag_id: tag_id,
			db: current_tgn,
			addif1: to_assign,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatetagassign",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=='backend mismatch') {
				console.log('updatetagassign not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Server version was updated.  Please refresh the page before further actions.");
			} else if (returnedbyserver.isok=='backend db mismatch') {
				console.log('updatetagassign not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Version of target notebook does not match the server db version.  Please resolve this before changing tag assignments to this notebook.");
			} else if (returnedbyserver.isok!="ok") {
				console.log('updatetagassign not successful');
				master_busy = false;
				alert("Problem updating tag assignment");
			} else {
				if (to_assign==0) {
					button.style.filter = 'grayscale(100%)';
					for (var j=0; j<all_set_tags[current_tgn].length; ++j) {
						if (all_set_tags[current_tgn][j]==tag_short) {all_set_tags[current_tgn].splice(j, 1); break;}
					}
				} else {
					button.style.filter = '';
					all_set_tags[current_tgn].push(tag_short);
				}
				button.setAttribute('data-is_assigned_to_tgn', to_assign);
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('updatetagassign not successful');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem updating tag assignment");
		}
	});
};

ToggleAssignToCurrentTag = function(button) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var is_currently_assigned = button.getAttribute('data-is_assigned_to_tag');
	var to_assign = 1 - is_currently_assigned;
	var whattodo = {
			todo: "updatetagassign",
			tag_id: current_tag_id,
			db: button.innerHTML.toLowerCase(),
			addif1: to_assign,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/updatetagassign",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=='backend mismatch') {
				console.log('updatetagassign not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Server version was updated.  Please refresh the page before further actions.");
			} else if (returnedbyserver.isok=='backend db mismatch') {
				console.log('updatetagassign not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Version of target notebook does not match the server db version.  Please resolve this before changing tag assignments to this notebook.");
			} else if (returnedbyserver.isok!="ok") {
				console.log('updatetagassign not successful');
				master_busy = false;
				alert("Problem updating tag assignment");
			} else {
				if (to_assign==0) {
					button.style.color = '';
					button.style.backgroundColor = '';
					for (var j=0; j<all_set_tags[button.innerHTML.toLowerCase()].length; ++j) {
						if (all_set_tags[button.innerHTML.toLowerCase()][j]==current_tag) {
							all_set_tags[button.innerHTML.toLowerCase()].splice(j, 1);
							break;
						}
					}
				} else {
					button.style.color = current_tag_text_color;
					button.style.backgroundColor = current_tag_color;
					all_set_tags[button.innerHTML.toLowerCase()].push(current_tag);
				}
				button.setAttribute('data-is_assigned_to_tag', to_assign);
				master_busy = false;
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('updatetagassign not successful');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem updating tag assignment");
		}
	});
};

function AddAssignByTGNOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "get_db_tag_assigns",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_db_tag_assigns",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				all_tgns.length=0;
				for (var prop in all_set_tags) {
					if (all_set_tags.hasOwnProperty(prop)) {
						delete all_set_tags[prop];
					}
				}
				var taobj = JSON.parse(returnedbyserver.db_tag_assigns);
				var num_dbs = taobj.length;
				for (var i=0; i<num_dbs; ++i) {
					var taobj2 = JSON.parse(taobj[i]);
					var dbname = taobj2.db;
					all_tgns.push(dbname);
					all_set_tags[dbname] = [];
					var taglist = JSON.parse(taobj2.tags);
					for (var j=0; j<taglist.length; ++j) {
						all_set_tags[dbname].push(taglist[j]);
					}
				}
				all_tgns.sort();
				var mydiv = document.getElementById("assignbytgnoverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="assignbytgndiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				dcontent += '<p style="color:#000; font-size:20px; margin-top:10px;">Edit Assignment By TGN</p>';
				dcontent += '<p style="color:#000;">';
				dcontent += '<select id="tgn_selector" style="width:200px" onchange="ChangedTGNSelector(this)">';
				current_tgn = all_tgns[0];
				for (var i=0; i<all_tgns.length; ++i) {
					dcontent += '<option value="'+escapeHtml(all_tgns[i])+'">'+escapeHtml(all_tgns[i].toUpperCase())+'</option>';
				}
				dcontent += '</select></p>';
				dcontent+='<p><div style="width:750px; margin: 0 auto; display: flex; justify-content: space-between;">';
				var alltagrows = document.getElementsByClassName("a_tag_row");
				for (var ij=0; ij<alltagrows.length; ++ij) {
					var sname = alltagrows[ij].getElementsByClassName("short_name")[0].innerHTML;
					var tid = alltagrows[ij].getAttribute('id');
					tid = tid.replace("tagid", "");
					var isrc = $('#tagid'+tid+' svg');
					var serializer = new XMLSerializer();
					var str = serializer.serializeToString(isrc[0]);
					if (all_set_tags[current_tgn].indexOf(sname)>-1) {
						dcontent += '<button style="padding:0px; border: 1px; height: 25px; display:block; margin:auto;" data-is_assigned_to_tgn="1" id="tag_button_'+tid+'" onclick="ToggleAssignToCurrentTGN(this)">'+str+'</button>';
					} else {
						dcontent += '<button style="padding:0px; border:1px; height: 25px; display:block; margin:auto; filter:grayscale(100%);" data-is_assigned_to_tgn="0" id="tag_button_'+tid+'" onclick="ToggleAssignToCurrentTGN(this)">'+str+'</button>';
					}
					if (ij%6==0 && ij>0 && ij<alltagrows.length-1) {
						dcontent+='</div></p>';
						dcontent+='<p><div style="width:750px; margin: 0 auto; display: flex; justify-content: space-between;">';
					}
				}
				dcontent+='</div></p>';
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				$('#tgn_selector').attr('data-ctgn', current_tgn);
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_assign_by_tgn_x_button');
				tmpb.setAttribute('onclick', 'RemoveAssignByTGNOverlay()');
				tmpb.setAttribute('class', 'btn btn-primary btn-sm');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				$('#tgn_selector').select2({minimumResultsForSearch: 20});
				mydiv.style.display = 'block';
				master_busy = false;		
			} else {
				console.log('problem recovering tag assignments');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem recovering tag assignments");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering tag assignments');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering tag assignments");
		}
	});
};

function AddAssignByTagOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "get_db_tag_assigns",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_db_tag_assigns",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				all_tgns.length=0;
				for (var prop in all_set_tags) {
					if (all_set_tags.hasOwnProperty(prop)) {
						delete all_set_tags[prop];
					}
				}
				var taobj = JSON.parse(returnedbyserver.db_tag_assigns);
				var num_dbs = taobj.length;
				for (var i=0; i<num_dbs; ++i) {
					var taobj2 = JSON.parse(taobj[i]);
					var dbname = taobj2.db;
					all_tgns.push(dbname);
					all_set_tags[dbname] = [];
					var taglist = JSON.parse(taobj2.tags);
					for (var j=0; j<taglist.length; ++j) {
						all_set_tags[dbname].push(taglist[j]);
					}
				}
				all_tgns.sort();
				var mydiv = document.getElementById("assignbytagoverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="assignbytagdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">Edit Assignment By Tag</p>';
				dcontent += '<p style="color:#000;">';
				dcontent += '<select id="tag_selector" style="width:130px" onchange="ChangedTagSelector(this)">';
				var alltagrows = document.getElementsByClassName("a_tag_row");
				for (var ij=0; ij<alltagrows.length; ++ij) {
					var sname = alltagrows[ij].getElementsByClassName("short_name")[0].innerHTML;
					var tid = alltagrows[ij].getAttribute('id');
					tid = tid.replace("tagid", "");
					if (ij==0) {
						current_tag = sname;
						current_tag_id = tid;
						current_tag_color = alltagrows[ij].getAttribute('data-tag_color');
						current_tag_text_color = alltagrows[ij].getAttribute('data-tag_text_color');
					}
					dcontent += '<option value="'+tid+'">'+sname+'</option>';
				}
				dcontent += '</select></p>';
				for (var i=0; i<all_tgns.length; i+=6) {
					dcontent+='<p><div style="width:750px; margin: 0 auto; display: flex; justify-content: space-between;">';
					for (var ii=0; ii<6; ++ii) {
						var iii = i + ii;
						if (iii<all_tgns.length) {
							var dbname = all_tgns[iii];
							if (all_set_tags[dbname].indexOf(current_tag)>-1) {
								dcontent += '<button style="display:block; width:100px; margin:auto; color:'+current_tag_text_color+' !important; background-color:'+current_tag_color+' !important;" class="ak_table_button" data-is_assigned_to_tag="1" id="db_button_'+escapeHtml(dbname)+'" onclick="ToggleAssignToCurrentTag(this)">'+escapeHtml(dbname.toUpperCase())+'</button>';
							} else {
								dcontent += '<button style="display:block; width:100px; margin:auto;" class="ak_table_button" data-is_assigned_to_tag="0" id="db_button_'+escapeHtml(dbname)+'" onclick="ToggleAssignToCurrentTag(this)">'+escapeHtml(dbname.toUpperCase())+'</button>';
							}
						}
					}
					dcontent+='</div></p>'
				}
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				$('#tag_selector').attr('data-ctid', current_tag_id);
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_assign_by_tag_x_button');
				tmpb.setAttribute('onclick', 'RemoveAssignByTagOverlay()');
				tmpb.setAttribute('class', 'btn btn-primary btn-sm');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				$('#tag_selector').select2({minimumResultsForSearch: 20, templateSelection: formatState, templateResult:formatState});
				mydiv.style.display = 'block';
				master_busy = false;
			} else {
				console.log('problem recovering tag assignments');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem recovering tag assignments");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering tag assignments');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering tag assignments");
		}
	});
};

function AddPushWebReferenceOverlay() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var whattodo = {
			todo: "get_all_db_list",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_all_db_list",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				all_tgns.length=0;
				var taobj = JSON.parse(returnedbyserver.db_name_list);
				var num_dbs = taobj.length;
				for (var i=0; i<num_dbs; ++i) {
					all_tgns.push(taobj[i]);				
				}
				all_tgns.sort(); // probably not needed
				var mydiv = document.getElementById("pushwebreferenceoverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="pushwebreferencediv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">Push Web Reference to Notebooks</p>';
				dcontent += '<p style="color:#000;">Year: <input class="ak_push_overlay" id="push_web_year" style="text-align:center;width:125px;color:#000;" placeholder="Eg. 2018" type="text" maxlength="4"></p>';
				dcontent += '<p style="color:#000;"><input id="push_web_site" placeholder="https://www.google.com" class="ak_push_overlay" style="text-align:center;width:450px" type="list" list="siteList"></p>';
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
				dcontent += '<p style="color:#000; vertical-align: middle;"><textarea id="push_web_description" rows="4" cols="48" style="color:#000; vertical-align: middle;" placeholder="Enter title" class="ak_push_overlay ak_tablecell" type="text"></textarea></p>';
				dcontent += '<button id="ak_push_button" style="margin-bottom:10px;" type="button" class="ak_push_overlay btn btn-primary btn-sm" onclick="PushToDBs()">Push</button>';
				dcontent += '<div style="width:600px; margin: 0 auto; display: flex; justify-content: space-between;">';
				dcontent += '<button style="display:block; margin:auto;" class="ak_table_button ak_push_overlay" id="select_all_button" onclick="SelectAllDBs(1)">Select All</button>';
				dcontent += '<button style="display:block; margin:auto;" class="ak_table_button ak_push_overlay" id="deselect_all_button" onclick="SelectAllDBs(0)">De-Select All</button>';	
					
				dcontent += '</div>';			
				dcontent += '<p style="color:#000; font-size: 20px;"></p>';
				dcontent += '<p style="color:#000;">';
				for (var i=0; i<all_tgns.length; i+=6) {
					dcontent+='<p><div style="width:750px; margin: 0 auto; display: flex; justify-content: space-between;">';
					for (var ii=0; ii<6; ++ii) {
						var iii = i + ii;
						if (iii<all_tgns.length) {
							var dbname = all_tgns[iii];
							dcontent += '<button style="display:block; width:100px; margin:auto; background-color:White;" class="ak_table_button ak_push_overlay" data-is_assigned_to_push="0" id="pdb_button_'+escapeHtml(dbname)+'" onclick="ToggleForWebPush(this)">'+escapeHtml(dbname.toUpperCase())+'</button>';
						}
					}
					dcontent+='</div></p>'
				}
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_push_to_db_x_button');
				tmpb.setAttribute('onclick', 'RemovePushWebReferenceToDBOverlay()');
				tmpb.setAttribute('class', 'btn btn-primary btn-sm ak_push_overlay');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				mydiv.style.display = 'block';
				master_busy = false;
			} else {
				console.log('problem recovering db list');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem recovering db list");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering db list');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering db list");
		}
	});
};

PushToDBs = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_push_overlay').attr('disabled', true);
	var push_web_year = document.getElementById("push_web_year").value;
	var push_web_site = document.getElementById("push_web_site").value;
	var push_web_description = document.getElementById("push_web_description").value;
	var push_to = [];
	for (var i=0; i<all_tgns.length; ++i) {
		var but = document.getElementById("pdb_button_"+all_tgns[i])
		if (but.getAttribute('data-is_assigned_to_push')==1) {
			push_to.push(all_tgns[i]);
		}
	}
	var whattodo = {
			todo: "pushweb",
			push_year: push_web_year,
			push_site: push_web_site,
			push_description: push_web_description,
			backend_version: backend_version,
			push_dbs: push_to
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/pushweb",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok!="ok") {
				console.log('pushweb not successful');
				$('.ak_push_overlay').attr('disabled', false);
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem pushing to dbs");
				}
			} else {
				$('.ak_push_overlay').attr('disabled', false);
				master_busy = false;
				alert(returnedbyserver.messages);
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('pushweb not successful');
			console.log(xhr, textStatus, errorThrown);
			$('.ak_push_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem pushing to dbs");
		}
	});
};

RemovePushWebReferenceToDBOverlay = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	var mydiv = document.getElementById("pushwebreferenceoverlay");
	mydiv.style.display = 'none';
}

RemoveAssignByTagOverlay = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	var mydiv = document.getElementById("assignbytagoverlay");
	mydiv.style.display = 'none';
}

RemoveAssignByTGNOverlay = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	var mydiv = document.getElementById("assignbytgnoverlay");
	mydiv.style.display = 'none';
}

function AddEditTagOverlay(current_tag_id) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	var current_tagclass_id = "-1";
	var current_color = "rgb(170,85,170)";
	var current_short = "";
	var current_long = "";
	var current_description = "";
	if (current_tag_id>-1) {
		current_short = $('#tagid'+current_tag_id+' .short_name').text();
		current_long = $('#tagid'+current_tag_id+' .long_name').val();
		current_description = $('#tagid'+current_tag_id+' .description').val();
		current_color = $('#tagid'+current_tag_id).attr("data-tag_color");
		current_tagclass_id = $('#tagid'+current_tag_id+' .class_name').attr("data-tagclass_id");
	}
	var whattodo = {
			todo: "get_tag_class_list",
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/get_tag_class_list",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				
				var pobj = JSON.parse(returnedbyserver.class_list);
				var mydiv = document.getElementById("edittagoverlay");
				mydiv.innerHTML = '';
				var dcontent = '<div id="edittagdiv" style="background-color:#eeeeee; width: 800px; display: inline-block; font-size: 12px;">';
				dcontent += '<p style="color:#000; font-size: 20px; margin-top:10px;">Create/Edit Tag</p>';
				dcontent += '<p style="color:#000;">Class:';
				var s_html = '<select style="width:200px" class="ak_tag_overlay ak_tag_class_selector" >';
				if (current_tagclass_id<0) {
					s_html += '<option selected disabled hidden style="display: none" value="">Please select</option>';
					for (var i=0; i<pobj.length; ++i) {
						s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].class_name)+'</option>';
					}
					s_html += '</select>';
				} else {
					for (var i=0; i<pobj.length; ++i) {
						if (current_tagclass_id==pobj[i].id) {
							s_html += '<option selected="selected" value="'+pobj[i].id+'">'+escapeHtml(pobj[i].class_name)+'</option>';
						} else {
							s_html += '<option value="'+pobj[i].id+'">'+escapeHtml(pobj[i].class_name)+'</option>';
						}
					}
					s_html += '</select>';
				}
				dcontent += s_html+'</p>';
				dcontent += '<p style="color:#000;">Short name: <input class="ak_tag_overlay" id="short_tag_name" style="text-align:center;width:100px;color:#000;" placeholder="Eg. Tag Name" type="text" maxlength="15"></p>';
				dcontent += '<p style="color:#000;">Long name: <input class="ak_tag_overlay" id="long_tag_name" style="text-align:center;width:250px;color:#000;" placeholder="Eg. Longer Tag Name" type="text" maxlength="150"></p>';
				dcontent += '<p><button id="tag_color_button" class="tag-overlay-color-button ak_table_button ak_tag_overlay">Tag Color</button></p>'
					dcontent += '<p style="color:#000; vertical-align: middle;">Description: <textarea id="tag_description" rows="4" cols="50" style="color:#000; vertical-align: middle;" placeholder="Enter description" class="ak_tag_overlay ak_tablecell" type="text"></textarea></p>';
				dcontent += '<button id="ak_save_tag_button" style="margin-bottom:10px;" data-tag_id="'+current_tag_id+'" type="button" class="ak_tag_overlay btn btn-primary btn-sm" onclick="SaveTag()">Save</button>';
				dcontent += "</div>";
				mydiv.innerHTML = dcontent;
				var tmpb = document.createElement('button');
				tmpb.setAttribute('id', 'ak_edit_tag_x_button');
				tmpb.setAttribute('onclick', 'RemoveEditTagOverlay()');
				tmpb.setAttribute('class', 'ak_tag_overlay btn btn-primary btn-sm');
				tmpb.style.verticalAlign='top';
				tmpb.style.display='inline-block';
				tmpb.innerHTML = 'X';
				mydiv.appendChild(tmpb);
				$(".ak_tag_class_selector").select2({minimumResultsForSearch: 20});
				var hueb = new Huebee( '.tag-overlay-color-button', {
					setText: false,
					className: 'light-picker'
				});
				hueb.setColor(current_color);
				if (current_tag_id>=0) {
					document.getElementById('short_tag_name').value=current_short;
					document.getElementById('long_tag_name').value=current_long;
					document.getElementById('tag_description').value=current_description;
				}
				mydiv.style.display = 'block';
				master_busy = false;
			} else {
				console.log('problem recovering tag classes');
				master_busy = false;
				if (returnedbyserver.isok=='backend mismatch') {
					alert("Server version was updated.  Please refresh the page before further actions.");
				} else {
					alert("Problem recovering tag classes");
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem recovering tag classes');
			console.log(xhr, textStatus, errorThrown);
			master_busy = false;
			alert("Problem recovering tag classes");
		}
	});
};

RemoveEditTagOverlay = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	var mydiv = document.getElementById("edittagoverlay");
	mydiv.style.display = 'none';
}

SaveTag = function() {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.ak_tag_overlay').attr('disabled', true);
	var short_name = document.getElementById("short_tag_name").value;
	var long_name = document.getElementById("long_tag_name").value;
	var description = document.getElementById("tag_description").value;
	var class_selector = document.getElementsByClassName("ak_tag_class_selector")[0];
	var tagclass_id = -1;
	if (class_selector.selectedIndex>0) {
		tagclass_id = class_selector.options[class_selector.selectedIndex].value;
	}
	var tag_color = document.getElementById("tag_color_button").style.backgroundColor;
	var tag_id = document.getElementById('ak_save_tag_button').getAttribute("data-tag_id");
	var whattodo = {
			todo: "savetag",
			tag_id: tag_id,
			tag_class_id: tagclass_id,
			short_name: short_name,
			long_name: long_name,
			description: description,
			color: tag_color,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/savetag",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=='backend mismatch') {
				console.log('savetag not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Server version was updated.  Please refresh the page before further actions.");
			} else if (returnedbyserver.isok!="ok") {
				console.log('savetag not successful');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Problem saving tag to db");
			} else {
				if (returnedbyserver.errors!="") {
					console.log('savetag had user errors at');
					$('.ak_tag_overlay').attr('disabled', false);
					master_busy = false;
					alert(returnedbyserver.errors);
				} else {
					var myrow = document.getElementById("tagid"+returnedbyserver.tagid);
					if (myrow==null) {
						var $row = $(returnedbyserver.trtext);
						$("#available_tags").find('tbody').append($row).trigger('addRows', [$row]);	
					} else {
						$("#tagid"+returnedbyserver.tagid).replaceWith(returnedbyserver.trtext);
						$("#available_tags").trigger('update');
					}
					$('.ak_tag_overlay').attr('disabled', false);
					$('#assign_by_tag_button').attr('disabled', false);
					$('#assign_by_tgn_button').attr('disabled', false);
					var mydiv = document.getElementById("edittagoverlay");
					mydiv.style.display = 'none';
					master_busy = false;
				}
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('save tag not successful');
			console.log(xhr, textStatus, errorThrown);
			$('.ak_tag_overlay').attr('disabled', false);
			master_busy = false;
			alert("Problem saving tag to db");
		}
	});
}

DeleteTag = function(tagid) {
	if (master_busy) {
		alert("Please re-try after background actions have completed.");
		return;
	}
	master_busy = true;
	$('.aktagrowclass'+tagid).attr('disabled', true);
	var whattodo = {
			todo: "deletetag",
			tag_id: tagid,
			backend_version: backend_version
	};
	$.ajax({
		url: window.location.protocol+"//"+window.location.host+"/deletetag",
		type: "post",
		timeout: 30000,
		data: JSON.stringify(whattodo),
		dataType: "json",
		contentType: "application/json",
		success: function(returnedbyserver){
			if (returnedbyserver.isok=="ok") {
				$("#tagid"+tagid).remove();
				$("#available_tags").trigger('update');
				var alltagrows = document.getElementsByClassName("a_tag_row");
				var num_tags = alltagrows.length;
				if (num_tags==0) {
					$('#assign_by_tag_button').attr('disabled', true);
					$('#assign_by_tgn_button').attr('disabled', true);
				}
				master_busy = false;
			} else if (returnedbyserver.isok=='backend mismatch') {
				console.log('problem removing tag');
				$('.ak_tag_overlay').attr('disabled', false);
				master_busy = false;
				alert("Server version was updated.  Please refresh the page before further actions.");
			} else if (returnedbyserver.isok!="notok"){
				$('.aktagrowclass'+tagid).attr('disabled', false);
				master_busy = false;
				alert(returnedbyserver.isok);
			} else {
				console.log('problem removing tag');
				$('.aktagrowclass'+tagid).attr('disabled', false);
				master_busy = false;
				alert("Problem removing tag");
			}
		},
		error: function(xhr, textStatus, errorThrown){
			console.log('problem removing tag');
			console.log(xhr, textStatus, errorThrown);
			$('.aktagrowclass'+tagid).attr('disabled', false);
			master_busy = false;
			alert("Problem removing tag");
		}
	});
};
