package com.tgn;

import org.w3c.dom.*;

import java.util.*;

class Layout {

	int svg_width = 1000; // in svg units, which will often be interpreted as pixels
	int svg_height = -1; // in svg_units, which will often be interpreted as pixels
	double variant_tick_min_pixel_width = 2.0;
	int fat_exon_height = 20;
	int transcript_bar_height = 10;
	int cds_bar_height = 6;
	int fat_exon_from_top = 10;
	int transcript_bar_from_top = 15;
	int cds_bar_from_top = 17;
	int gene_name_from_top = 51;
	int gene_line_height = 60;
	int first_plus_strand_gene_top = 200; // was 100
	int kb_track_height = 35;
	int kb_tick_from_top = 20;
	int kb_name_from_top = 15;
	int first_minus_strand_gene_top = first_plus_strand_gene_top + gene_line_height;
	int footprint_end_buffer = 100;
	int chrom_low = -1; // 1-based
	int tss = -1; // 1-based
	int tss_dir = 0;
	int chrom_high = -1; // 1-based
	int width_in_bases = -1; // number of full bases included .. if chrom_low==chrom_high, then width_in_bases=1
	double svg_units_per_base = -1.0; 
	int alt_viewbox_svg_x = -1;
	int alt_viewbox_svg_width = -1;
	double alt_viewbox_fraction_of_full_field = -1.0;
	String alt_viewbox_click_varname = "";
	int min_svg_footprint_for_tx = 250;
	double alt_viewbox_base_start = -1.0;
	double alt_viewbox_base_width = -1.0;
	double alt_viewbox_pixels_per_base = -1.0;
	Vector<BitSet> plus_footprints = new Vector<>();
	Vector<BitSet> minus_footprints = new Vector<>();
	int gap_count = 0;
	int block_count = 0;
	int square_size = 0;
	int box_start_y_offset = 100;
	int ld_marker_tick_size = 20;

	void PlaceVertical(GeneAnnotation ga) {
		Integer tx_start = ga.exon_starts.get(0);
		Integer tx_end = ga.exon_ends.get(ga.num_exons-1);
		if (tx_start>tx_end) {
			tx_start = ga.exon_starts.get(ga.num_exons-1);
			tx_end = ga.exon_ends.get(0);
		}

		Vector<BitSet> tmp_bs = plus_footprints;
		if (ga.is_forward_strand!=1) {
			tmp_bs = minus_footprints;
		}

		boolean found = false;
		int low_index = tx_start-chrom_low;
		if (low_index<0) low_index = 0;
		int high_index = tx_end-chrom_low;

		if (high_index<0) return;
		if (low_index>(chrom_high-chrom_low)) return;

		int center_index = (low_index+high_index)/2;

		if (((double)high_index-(double)center_index)*svg_units_per_base<(double)min_svg_footprint_for_tx/2.0) {
			high_index = (int)(.5+(double)center_index+(double)min_svg_footprint_for_tx/(2.0*svg_units_per_base));
		}

		if (((double)center_index-(double)low_index)*svg_units_per_base<(double)min_svg_footprint_for_tx/2.0) {
			low_index = (int)(.5+(double)center_index-(double)min_svg_footprint_for_tx/(2.0*svg_units_per_base));
			if (low_index<0) low_index=0;
		}

		for (int i=0; i<tmp_bs.size(); ++i) {
			BitSet bs = tmp_bs.get(i);
			int nh = bs.nextSetBit(low_index);
			if (nh<0 || nh>high_index) {
				found = true;
				int low = low_index - footprint_end_buffer;
				if (low<0) low=0;
				int high = high_index + footprint_end_buffer;
				bs.set(low, (high+1));
				ga.which_gene_line = i+1;
				break;
			}
		}

		if (!found) {
			BitSet bs = new BitSet();
			int low = (tx_start - footprint_end_buffer)-chrom_low;
			if (low<0) low = 0;
			int high = (tx_end + footprint_end_buffer)-chrom_low;
			bs.set(low, (high+1));
			ga.which_gene_line = tmp_bs.size()+1;
			tmp_bs.add(bs);
		}

	}

	void KBTrackLayout(GeneAnnotation ga, Document d, Element par) {

		Integer tx_low = ga.exon_starts.get(0);
		Integer tx_high = ga.exon_ends.get(ga.num_exons-1);
		boolean positive_strand = true;
		if (tx_low>tx_high) {
			tx_low = ga.exon_starts.get(ga.num_exons-1);
			tx_high = ga.exon_ends.get(0);
			positive_strand = false;
		}
		int jump = 25000;
		while (((double)chrom_high-(double)chrom_low)/(double)jump>10.0) {
			jump *= 2;
		}
		int tloc = tx_low - jump;	
		if (!positive_strand) tloc = tx_high + jump;
		int tloc_delta_kb = -jump/1000;

		// move upstream on coding strand

		while (true) {
			if (positive_strand && tloc<=chrom_low+(int)(0.5*jump)) break;
			if (!positive_strand && tloc>=chrom_high-(int)(0.5*jump)) break;
			if (positive_strand) tloc-=jump;
			else tloc+=jump;
			tloc_delta_kb-=jump/1000;
		}
		if (positive_strand) tloc+=jump;
		else tloc-=jump;
		tloc_delta_kb+=jump/1000;
		int ctr = 1;
		while (true) {
			if (positive_strand && tloc>=chrom_high-(int)(0.5*jump)) break;
			if (!positive_strand && tloc<=chrom_low+(int)(0.5*jump)) break;
			String spotname = tloc_delta_kb+"";
			if (tloc_delta_kb>0) spotname="+"+spotname;
			if (tloc_delta_kb!=0) spotname+="kb";
			String col = "#c60071";			
			double spot_start_svg = (double)((tloc - chrom_low))*svg_units_per_base;
			if (spot_start_svg<0.0) spot_start_svg = 0.0;
			double spot_end_svg = (double)(((tloc+1) - chrom_low))*svg_units_per_base;
			if (spot_end_svg>(double)svg_width) spot_end_svg = (double)svg_width;
			double spot_center_svg = (spot_end_svg+spot_start_svg)/2.0;
			int spot_base_width = 1;
			double spot_base_center = ((double)(tloc))+0.5;
			Element group_for_a_spot = SvgGraphics.makeNode(d,
					par,//parent
					"g",//type
					null
					);
			String spotid = "akspot_"+ctr;
			double width_in_pixels = (double)spot_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
			double scaled_width_in_pixels = width_in_pixels;
			double svg_startx = spot_center_svg - 0.5*scaled_width_in_pixels;
			if (scaled_width_in_pixels<variant_tick_min_pixel_width) {
				scaled_width_in_pixels = variant_tick_min_pixel_width;
				svg_startx = spot_center_svg - 0.5*scaled_width_in_pixels;
				int ssi = (int)svg_startx;
				double ssifr = svg_startx - (double)ssi;
				if (ssifr>.5) svg_startx = (double)ssi+1.0;
				else svg_startx = (double)ssi;
			}
			Element ele = SvgGraphics.makeNode(d,
					group_for_a_spot,//parent
					"rect",//type
					new String[]{"x",""+svg_startx,
							"y",""+(kb_tick_from_top),
							"width",""+scaled_width_in_pixels,
							"height","10",
							"id", spotid,
							"class", "kbspot",
							"data-spotbasewidth", ""+spot_base_width,
							"data-spotbasecenter", ""+spot_base_center,
							"data-spotsvgcenter", ""+spot_center_svg,
							"fill",col
			});
			ele = SvgGraphics.makeNode(d,
					group_for_a_spot,//parent
					"text",//type
					new String[]{"x",""+spot_center_svg,
							"y",""+(kb_name_from_top),
							"font-weight","400",
							"class","akspotname",
							"data-origsvgx",""+spot_center_svg,
							"font-size","13",
							"fill",col,
							"cursor","default",
							"text-anchor","middle",
							"font-family","Lato, Sans-serif"
			});
			ele.setTextContent(spotname);
			if (positive_strand) tloc+=jump;
			else tloc-=jump;
			tloc_delta_kb+=jump/1000;
			ctr++;
		}

	}

	void AssocLayout(Vector<String> index_variants, Hashtable<String, IndexVariant> assoc_result_hash, Document d, Element par, String svg_mode) {

		String vis = "visible";
		if (svg_mode.equals("LD Summary")) vis = "hidden";

		Element group_for_mes = SvgGraphics.makeNode(d,
				par,//parent
				"g",//type
				new String[]{"visibility", vis, "id", "group_for_mes"}
				);
		int current_top = (minus_footprints.size()+plus_footprints.size())*gene_line_height+kb_track_height;

		/*
		 * Each index_variant has gwas and/or eqtl results
		 */
		
		Vector<String> sorted_index_variants = new Vector<>();
		Vector<Integer> sorted_locations = new Vector<>();
		
		for (int i=0; i<index_variants.size(); ++i) {
			String iv = index_variants.get(i);
			int firstsp = iv.indexOf(" ");
			Integer startp = Integer.parseInt(iv.substring(firstsp+1, iv.indexOf(" ", firstsp+1)));
			int loc = -1;
			for (int j=0; j<sorted_locations.size(); ++j) {
				Integer tst = sorted_locations.get(j);
				if (tst>startp) {
					loc = j;
					sorted_locations.insertElementAt(startp, j);
					sorted_index_variants.insertElementAt(iv, j);
					break;
				} else  if (tst==startp) {
					String iv2 = sorted_index_variants.get(j);
					if (iv.compareTo(iv2)<0) {
						sorted_locations.insertElementAt(startp, j);
						sorted_index_variants.insertElementAt(iv, j);
						loc = j;
					} else {
						sorted_locations.insertElementAt(startp, j+1);
						sorted_index_variants.insertElementAt(iv, j+1);
						loc = j+1;
					}
					break;
				}
			}
			if (loc==-1) {
				sorted_locations.add(startp);
				sorted_index_variants.add(iv);
			}
		}
		
		
		// try to group by member snps
		
		if (sorted_index_variants.size()>2) {
			int number_index_variants = sorted_index_variants.size();
			Vector<Vector<String>> rses = new Vector<>();
			for (int i=0; i<sorted_index_variants.size(); ++i) {
				IndexVariant giv  = assoc_result_hash.get(sorted_index_variants.get(i));
				Vector<String> vn = new Vector<>();
				vn.add(giv.index_variant_name);
				for (int j=0; j<giv.gwas_results.size(); ++j) {
					GWASResult g = giv.gwas_results.get(j);
					if (!g.marker_equivalence_set.equals("Unset") && g.show_in_svg) {
						for (int k=0; k<g.mes_locs_starts.size(); ++k) {
							String n = g.mes_variants.get(k);
							if (!vn.contains(n)) vn.add(n);
						}
					}
				}
				for (int j=0; j<giv.eqtl_results.size(); ++j) {
					EQTLResult q = giv.eqtl_results.get(j);
					if (!q.show_in_svg) continue;
					for (int k=0; k<q.ld_locs_starts.size(); ++k) {
						String n = q.ld_variants.get(k);
						if (!vn.contains(n)) vn.add(n);
					}
				}
				rses.addElement(vn);
			}
			
			Integer max_count = 0;
			String max_key = "";
			
			Hashtable<String, Integer> overlap = new Hashtable<>();
			for (int i=0; i<sorted_index_variants.size()-1; ++i) {
				Vector<String> v1 = rses.get(i);
				for (int j=i+1; j<sorted_index_variants.size(); ++j) {
					Vector<String> v2 = rses.get(j);
					int match = 0;
					for (int k=0; k<v1.size(); ++k) {
						String r = v1.get(k);
						if (v2.contains(r)) match++;
					}
					String key = i+"-"+j;
					overlap.put(key, Integer.valueOf(match));
					if (match>max_count) {
						max_count = match;
						max_key = key;
					}
				}	
			}
			
			
			if (!max_key.equals("")) {
				BitSet used = new BitSet(number_index_variants);
				
				// keep making islands ...
				Vector<Vector<Integer>> all_islands = new Vector<>();
				while (true) {
					
					Set<String> overlap_keys = overlap.keySet();
					
					// find largest overlap pair
					Integer largest = 0;
					String largest_key = "";
					for (String key : overlap_keys) {
						String[] idxs = key.split("-");
						if (!used.get(Integer.parseInt(idxs[0])) && !used.get(Integer.parseInt(idxs[1]))) {
							if (overlap.get(key)>largest) {
								largest = overlap.get(key);
								largest_key = key;
							}
						}
					}
					if (largest_key.equals("")) break;
					
					Vector<Integer> island = new Vector<>();
					String[] idxs = largest_key.split("-");
					Integer low = Integer.parseInt(idxs[0]);
					Integer high = Integer.parseInt(idxs[1]);
					
					island.add(low);
					island.add(high);
					
					used.set(low);
					used.set(high);
					
					while (true) {
						Integer low_end = island.get(0);
						Integer lgest = 0;
						String lgest_key = "";
						for (String key : overlap_keys) {
							String[] ixs = key.split("-");
							Integer ix0 = Integer.parseInt(ixs[0]);
							Integer ix1 = Integer.parseInt(ixs[1]);
							Integer chk_idx = -1;
							if (ix0==low_end) chk_idx = ix1;
							else if (ix1==low_end) chk_idx = ix0;
							if (chk_idx!=-1 && !used.get(chk_idx)) {
								if (overlap.get(key)>lgest) {
									lgest = overlap.get(key);
									lgest_key = key;
								}
							}
						}
							
						if (lgest_key.equals("")) break;
						String[] ixs = lgest_key.split("-");
						Integer ix0 = Integer.parseInt(ixs[0]);
						Integer ix1 = Integer.parseInt(ixs[1]);
						Integer use_idx = -1;
						if (ix0==low_end) use_idx = ix1;
						else if (ix1==low_end) use_idx = ix0;
						island.insertElementAt(use_idx, 0);
						used.set(use_idx);
					}
					
					while (true) {
						Integer high_end = island.get(island.size()-1);
						Integer lgest = 0;
						String lgest_key = "";
						for (String key : overlap_keys) {
							String[] ixs = key.split("-");
							Integer ix0 = Integer.parseInt(ixs[0]);
							Integer ix1 = Integer.parseInt(ixs[1]);
							Integer chk_idx = -1;
							if (ix0==high_end) chk_idx = ix1;
							else if (ix1==high_end) chk_idx = ix0;
							if (chk_idx!=-1 && !used.get(chk_idx)) {
								if (overlap.get(key)>lgest) {
									lgest = overlap.get(key);
									lgest_key = key;
								}
							}
						}
							
						if (lgest_key.equals("")) break;
						String[] ixs = lgest_key.split("-");
						Integer ix0 = Integer.parseInt(ixs[0]);
						Integer ix1 = Integer.parseInt(ixs[1]);
						Integer use_idx = -1;
						if (ix0==high_end) use_idx = ix1;
						else if (ix1==high_end) use_idx = ix0;
						island.add(use_idx);
						used.set(use_idx);
					}
					all_islands.add(island);
				}
			
				Vector<Integer> final_order = new Vector<>();
				used.clear();
				for (Integer i = 0; i<number_index_variants; ++i) {
					if (used.get(i)) continue;
					boolean added = false;
					for (int j=0; j<all_islands.size(); ++j) {
						Vector<Integer> island = all_islands.get(j);
						boolean add = false;
						for (int k=0; k<island.size(); ++k) {
							Integer idx = island.get(k);
							if (idx==i) {
								add = true;
								break;
							}
						}	
						if (!add) continue;
						for (int k=0; k<island.size(); ++k) {
							Integer idx = island.get(k);
							final_order.add(idx);
							used.set(idx);
							added = true;
						}
						if (added) break;
					}
					if (!added) {
						final_order.add(i);
						used.set(i);
					}
				}
				Vector<String> new_order = new Vector<>();
				for (int i=0; i<final_order.size(); ++i) {
					new_order.add(sorted_index_variants.get(final_order.get(i)));
				}
				sorted_index_variants = new_order;
			}
		}
		
		
		
		for (int i=0; i<sorted_index_variants.size(); ++i) {
			
			IndexVariant giv  = assoc_result_hash.get(sorted_index_variants.get(i));
			double index_var_svg_low = (double)((giv.index_variant_location_start - chrom_low))*svg_units_per_base;
			double index_var_svg_high = (double)(((giv.index_variant_location_end+1) - chrom_low))*svg_units_per_base;

			// below line will cause problems for insertions, which could have a net width of <1 .. FIX!
			int index_var_base_width = (giv.index_variant_location_end-giv.index_variant_location_start)+1;
			double index_var_svg_center = 0.5*(index_var_svg_low+index_var_svg_high);
			double index_var_base_center = ((double)(giv.index_variant_location_end+giv.index_variant_location_start+1))*0.5;
			double width_in_pixels = (double)index_var_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
			double iv_scaled_width_in_pixels = width_in_pixels;
			double svg_startx = index_var_svg_center - 0.5*iv_scaled_width_in_pixels;
			if (iv_scaled_width_in_pixels<variant_tick_min_pixel_width) {
				iv_scaled_width_in_pixels = variant_tick_min_pixel_width;
				svg_startx = index_var_svg_center - 0.5*iv_scaled_width_in_pixels;
				int ssi = (int)svg_startx;
				double ssifr = svg_startx - (double)ssi;
				if (ssifr>.5) svg_startx = (double)ssi+1.0;
				else svg_startx = (double)ssi;
			}

			// do gwas first

			String varname = "gwas"+giv.index_variant_name;
			
			// collect the different gwas LD datasets found for the index variant
			
			Vector<String>found_ld_datasets = new Vector<>();
			
			
			Vector<GWASResult> sorted_gwas_results = new Vector<>();
			Vector<String> sorted_sdns = new Vector<>();
			for (int ii=0; ii<giv.gwas_results.size(); ++ii) {
				GWASResult g = giv.gwas_results.get(ii);
				String sdn = g.svg_display_name;
				int loc = -1;
				for (int j=0; j<sorted_sdns.size(); ++j) {
					String tst = sorted_sdns.get(j);
					if (tst.compareTo(sdn)>0) {
						loc = j;
						sorted_sdns.insertElementAt(sdn, j);
						sorted_gwas_results.insertElementAt(g, j);
						break;
					}
				}
				if (loc==-1) {
					sorted_sdns.add(sdn);
					sorted_gwas_results.add(g);
				}
			}
			
			
			for (int j=0; j<giv.gwas_results.size(); ++j) {
				GWASResult g = giv.gwas_results.get(j);
				if (!g.marker_equivalence_set.equals("Unset") && !g.marker_equivalence_set.equals("Credible set") && g.show_in_svg) {
					if (!found_ld_datasets.contains(g.marker_equivalence_set)) found_ld_datasets.add(g.marker_equivalence_set);
				}
			}
			
			Collections.sort(found_ld_datasets);
			
			// for each found LD dataset (other than Unset and Credible set)
			
			for (int ii=0; ii<found_ld_datasets.size(); ++ii) {
				String fld = found_ld_datasets.get(ii);
				Element group_for_gwas_hit = SvgGraphics.makeNode(d,
						group_for_mes,//parent
						"g",//type
						null
						);
				boolean did_first_one = false;
				for (int j=0; j<sorted_gwas_results.size(); ++j) {
					GWASResult g = sorted_gwas_results.get(j);
					if (g.marker_equivalence_set.equals(fld) && g.show_in_svg) {
						if (!did_first_one) {
							
							// draw the marker ticks for the LD dataset
							
							did_first_one = true;
							String ttt = giv.index_variant_name;
							String fillcolor = "#333333";
							if (giv.is_coding) {
								fillcolor = "#FF0000";
							}
							Element ele = SvgGraphics.makeNode(d,
									group_for_gwas_hit,//parent
									"rect",//type
									new String[]{"x",""+svg_startx,
											"y",""+(current_top),
											"width",""+iv_scaled_width_in_pixels,
											"height","20",
											"id", varname+" "+ii,
											"class", "gwasvariant index_"+giv.index_variant_name,
											"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(giv.index_variant_name)+"')",
											"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
											"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(giv.index_variant_name)+"', evt)",
											"data-variationbasewidth", ""+index_var_base_width,
											"data-variationbasecenter", ""+index_var_base_center,
											"data-variationsvgcenter", ""+index_var_svg_center,
											"fill", fillcolor,
											"data-normalfill", fillcolor
							});
							for (int k=0; k<g.mes_locs_starts.size(); ++k) {
								Integer spot_start = g.mes_locs_starts.get(k);
								Integer spot_end = g.mes_locs_ends.get(k);
								String is_coding = g.mes_coding.get(k);
								double var_svg_low = (double)((spot_start - chrom_low))*svg_units_per_base;
								double var_svg_high = (double)(((spot_end+1) - chrom_low))*svg_units_per_base;
								int var_base_width = (spot_end-spot_start)+1;
								double var_svg_center = 0.5*(var_svg_low+var_svg_high);
								double var_base_center = ((double)(spot_end+spot_start+1))*0.5;
								varname = "gwasld_"+giv.index_variant_name+"_"+g.mes_variants.get(k)+"_"+ii;
								width_in_pixels = (double)var_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
								double var_scaled_width_in_pixels = width_in_pixels;
								double var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
								if (var_scaled_width_in_pixels<variant_tick_min_pixel_width) {
									var_scaled_width_in_pixels = variant_tick_min_pixel_width;
									var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
									int ssi = (int)var_svg_startx;
									double ssifr = var_svg_startx - (double)ssi;
									if (ssifr>.5) var_svg_startx = (double)ssi+1.0;
									else var_svg_startx = (double)ssi;
								}
								ttt=g.mes_variants.get(k)+"#r2 = "+g.mes_r2.get(k);
								double min_r2 = .5;
								double what_r2 = Double.parseDouble(g.mes_r2.get(k));
								double tlength = 3.0 + 9.0*(what_r2-min_r2)/(1.0-min_r2);
								double ttop = 4.0 + (12.0 - tlength)/2.0;
								ttop = 20.0-tlength;
								String gcol = "#333333";
								if (is_coding.equals("1")) {
									gcol = "#FF0000";
								}
								ele = SvgGraphics.makeNode(d,
										group_for_gwas_hit,//parent
										"rect",//type
										new String[]{"x",""+var_svg_startx,
												"y",""+(current_top+ttop),
												"width",""+var_scaled_width_in_pixels,
												"height",""+tlength,
												"id", varname,
												"class", "gwasvariant "+g.mes_variants.get(k),
												"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(g.mes_variants.get(k))+"')",
												"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
												"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(g.mes_variants.get(k))+"', evt)",
												"data-variationbasewidth", ""+var_base_width,
												"data-variationbasecenter", ""+var_base_center,
												"data-variationsvgcenter", ""+var_svg_center,
												"fill",gcol,
												"data-normalfill", gcol
								});
							}
							current_top+=20;
						}
						varname = "svggwas_"+g.gwas_table_row_id;
						String ttt = "";
						String ttt_type = "0";
						if (!g.marker_equivalence_set.equals("None") && g.typed_in_accepted_dataset) {
							ttt_type = "1";
							ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
							ttt+="<p>"+g.svg_display_name+"</p>";					
							ttt+="<p>Credible Set: "+g.marker_equivalence_set+"</p>";
							ttt+="<table id=\"aktmptable\" style=\"width:300px\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">r2</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding</th></tr></thead>";
							ttt+="<tbody>";
							boolean index_placed = false;
							for (int k=0; k<g.mes_locs_starts.size(); ++k) {
								Integer spot_start = g.mes_locs_starts.get(k);
								if (!index_placed) {
									if (k==0) {
										if (giv.index_variant_location_start<spot_start) {
											if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td>Y</td></tr>";
											else ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td></td></tr>";
											index_placed = true;
										}
									}
								}
								if (g.mes_coding.get(k).equals("1")) ttt+="<tr><td>"+g.mes_variants.get(k)+"</td><td>"+String.format("%.3f", Double.valueOf(g.mes_r2.get(k)))+"</td><td>Y</td></tr>";
								else ttt+="<tr><td>"+g.mes_variants.get(k)+"</td><td>"+String.format("%.3f", Double.valueOf(g.mes_r2.get(k)))+"</td><td></td></tr>";
								if (!index_placed) {
									if (k==g.mes_locs_starts.size()-1) {
										if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td>Y</td></tr>";
										else ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td></td></tr>";
										index_placed = true;
									} else if (giv.index_variant_location_start>=spot_start && giv.index_variant_location_start<g.mes_locs_starts.get(k+1)) {
										if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td>Y</td></tr>";
										else ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td></td></tr>";
										index_placed = true;
									}
								}	    
							}
							if (!index_placed) {
								if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td>Y</td></tr>";
								else ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td></td></tr>";
							}
							ttt+="</tbody>";
							ttt+="</table>";
							ttt+="</div>";
						} else if (g.marker_equivalence_set.equals("None")) {
							ttt_type = "1";
							ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
							ttt+="<p>"+g.svg_display_name+"</p>";					
							ttt+="<p>Credible Set: "+g.marker_equivalence_set+"</p>";
							ttt+="<table id=\"aktmptable\" style=\"width:300px\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">r2</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding</th></tr></thead>";
							ttt+="<tbody>";
							if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td>Y</td></tr>";
							else ttt+="<tr><td>"+giv.index_variant_name+"</td><td></td><td></td></tr>";
							ttt+="</tbody>";
							ttt+="</table>";
							ttt+="</div>";
						} else if (!g.typed_in_accepted_dataset) {
							ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
							ttt+="<p>"+g.svg_display_name+"</p>";					
							ttt+="<p>Credible Set: "+g.marker_equivalence_set+"</p>";
							ttt+="<p>(not typed)</p>";
							ttt+="</div>";
						}
						String what_weight = "400";
						Element ele = SvgGraphics.makeNode(d,
								group_for_gwas_hit,
								"text",//type
								new String[]{"x",""+index_var_svg_center,
										"y",""+(current_top+15),
										"id", varname,
										"font-weight",what_weight,
										"font-size","14",
										"class","akgwastext",
										"data-origsvgx",""+index_var_svg_center,
										"font-style","italic",
										"cursor","default",
										"text-anchor","middle",
										"font-family","Lato, Sans-serif",
										"data-supl", ttt,
										"data-supltype", ttt_type,
										"onclick", "populatesupldiv2(evt)"
						});
						ele.setTextContent(g.svg_display_name);
						current_top+=20;
					}
				}
			}

			// for any custom credible set gwas result for this index variant
			
			for (int j=0; j<sorted_gwas_results.size(); ++j) {
				GWASResult g = sorted_gwas_results.get(j);
				if (g.marker_equivalence_set.equals("Credible set") && g.show_in_svg) {
					Element group_for_gwas_hit = SvgGraphics.makeNode(d,
							group_for_mes,//parent
							"g",//type
							null
							);
					String ttt = giv.index_variant_name+"#Posterior = "+g.credible_set_posterior;
					String fillcolor = "#333333";
					if (giv.is_coding) {
						fillcolor = "#FF0000";
					}
					Element ele = SvgGraphics.makeNode(d,
							group_for_gwas_hit,//parent
							"rect",//type
							new String[]{"x",""+svg_startx,
									"y",""+(current_top),
									"width",""+iv_scaled_width_in_pixels,
									"height","20",
									"id", varname+" cs",
									"class", "gwasvariant index_"+giv.index_variant_name,
									"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(giv.index_variant_name)+"')",
									"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
									"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(giv.index_variant_name)+"', evt)",
									"data-variationbasewidth", ""+index_var_base_width,
									"data-variationbasecenter", ""+index_var_base_center,
									"data-variationsvgcenter", ""+index_var_svg_center,
									"fill", fillcolor,
									"data-normalfill", fillcolor
					});
					for (int k=0; k<g.mes_locs_starts.size(); ++k) {
						Integer spot_start = g.mes_locs_starts.get(k);
						Integer spot_end = g.mes_locs_ends.get(k);
						String is_coding = g.mes_coding.get(k);
						double var_svg_low = (double)((spot_start - chrom_low))*svg_units_per_base;
						double var_svg_high = (double)(((spot_end+1) - chrom_low))*svg_units_per_base;
						int var_base_width = (spot_end-spot_start)+1;
						double var_svg_center = 0.5*(var_svg_low+var_svg_high);
						double var_base_center = ((double)(spot_end+spot_start+1))*0.5;
						varname = "gwasld_"+giv.index_variant_name+"_"+g.mes_variants.get(k)+"_cc";
						width_in_pixels = (double)var_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
						double var_scaled_width_in_pixels = width_in_pixels;
						double var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
						if (var_scaled_width_in_pixels<variant_tick_min_pixel_width) {
							var_scaled_width_in_pixels = variant_tick_min_pixel_width;
							var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
							int ssi = (int)var_svg_startx;
							double ssifr = var_svg_startx - (double)ssi;
							if (ssifr>.5) var_svg_startx = (double)ssi+1.0;
							else var_svg_startx = (double)ssi;
						}
						ttt=g.mes_variants.get(k)+"#Posterior = "+g.mes_posterior.get(k);
						double min_r2 = 0.0;
						double what_r2 = Double.parseDouble(g.mes_posterior.get(k));
						double tlength = 5.0 + 7.0*(what_r2-min_r2)/(1.0-min_r2);
						double ttop = 4.0 + (12.0 - tlength)/2.0;
						ttop = 20.0-tlength;
						String gcol = "#333333";
						if (is_coding.equals("1")) {
							gcol = "#FF0000";
						}
						ele = SvgGraphics.makeNode(d,
								group_for_gwas_hit,//parent
								"rect",//type
								new String[]{"x",""+var_svg_startx,
										"y",""+(current_top+ttop),
										"width",""+var_scaled_width_in_pixels,
										"height",""+tlength,
										"id", varname,
										"class", "gwasvariant "+g.mes_variants.get(k),
										"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(g.mes_variants.get(k))+"')",
										"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
										"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(g.mes_variants.get(k))+"', evt)",
										"data-variationbasewidth", ""+var_base_width,
										"data-variationbasecenter", ""+var_base_center,
										"data-variationsvgcenter", ""+var_svg_center,
										"fill",gcol,
										"data-normalfill", gcol
						});

					}
					current_top+=20;
					varname = "svggwas_"+g.gwas_table_row_id;
					ttt = "";
					String ttt_type = "1";
					ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
					ttt+="<p>"+g.svg_display_name+"</p>";					
					ttt+="<p>Credible Set: "+g.credible_set_name+"</p>";
					ttt+="<table id=\"aktmptable\" style=\"width:300px\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">Posterior</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding</th></tr></thead>";
					ttt+="<tbody>";
					boolean index_placed = false;
					for (int k=0; k<g.mes_locs_starts.size(); ++k) {
						Integer spot_start = g.mes_locs_starts.get(k);
						if (!index_placed) {
							if (k==0) {
								if (giv.index_variant_location_start<spot_start) {
									if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td>Y</td></tr>";
									else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td></td></tr>";
									index_placed = true;
								}
							}
						}

						if (g.mes_coding.get(k).equals("1")) ttt+="<tr><td>"+g.mes_variants.get(k)+"</td><td>"+String.format("%.3f", Double.valueOf(g.mes_posterior.get(k)))+"</td><td>Y</td></tr>";
						else ttt+="<tr><td>"+g.mes_variants.get(k)+"</td><td>"+String.format("%.3f", Double.valueOf(g.mes_posterior.get(k)))+"</td><td></td></tr>";
						if (!index_placed) {
							if (k==g.mes_locs_starts.size()-1) {
								if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td>Y</td></tr>";
								else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td></td></tr>";
								index_placed = true;
							} else if (giv.index_variant_location_start>=spot_start && giv.index_variant_location_start<g.mes_locs_starts.get(k+1)) {
								if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td>Y</td></tr>";
								else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td></td></tr>";
								index_placed = true;
							}
						}	    
					}
					if (!index_placed) {
						if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td>Y</td></tr>";
						else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.3f", Double.valueOf(g.credible_set_posterior))+"</td><td></td></tr>";
					}
					ttt+="</tbody>";
					ttt+="</table>";
					ttt+="</div>";
					String what_weight = "400";
					ele = SvgGraphics.makeNode(d,
							group_for_gwas_hit,
							"text",//type
							new String[]{"x",""+index_var_svg_center,
									"y",""+(current_top+15),
									"id", varname,
									"font-weight",what_weight,
									"font-size","14",
									"class","akgwastext",
									"data-origsvgx",""+index_var_svg_center,
									"font-style","italic",
									"cursor","default",
									"text-anchor","middle",
									"font-family","Lato, Sans-serif",
									"data-supl", ttt,
									"data-supltype", ttt_type,
									"onclick", "populatesupldiv2(evt)"
					});
					ele.setTextContent(g.svg_display_name);
					current_top+=20;
				}
			}
			
			// do eqtls

			varname = "eqtl"+giv.index_variant_name;

			
			Vector<EQTLResult> sorted_eqtl_results = new Vector<>();
			sorted_sdns.clear();
			for (int ii=0; ii<giv.eqtl_results.size(); ++ii) {
				EQTLResult g = giv.eqtl_results.get(ii);
				String sdn = g.svg_display_name;
				int loc = -1;
				for (int j=0; j<sorted_sdns.size(); ++j) {
					String tst = sorted_sdns.get(j);
					if (tst.compareTo(sdn)>0) {
						loc = j;
						sorted_sdns.insertElementAt(sdn, j);
						sorted_eqtl_results.insertElementAt(g, j);
						break;
					}
				}
				if (loc==-1) {
					sorted_sdns.add(sdn);
					sorted_eqtl_results.add(g);
				}
			}
			
			
			for (int j=0; j<sorted_eqtl_results.size(); ++j) {
				EQTLResult q = sorted_eqtl_results.get(j);
				if (!q.show_in_svg) continue;
				Element group_for_gwas_hit = SvgGraphics.makeNode(d,
						group_for_mes,//parent
						"g",//type
						null
						);
				String ttt = giv.index_variant_name+"#Pvalue = "+q.pvalue+"#Beta = "+q.beta;
				String fillcolor = "#333333";
				if (giv.is_coding) {
					fillcolor = "#FF0000";
				}
				Element ele = SvgGraphics.makeNode(d,
						group_for_gwas_hit,//parent
						"rect",//type
						new String[]{"x",""+svg_startx,
								"y",""+(current_top),
								"width",""+iv_scaled_width_in_pixels,
								"height","20",
								"id", varname+" e",
								"class", "eqtlvariant index_"+giv.index_variant_name,
								"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(giv.index_variant_name)+"')",
								"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
								"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(giv.index_variant_name)+"', evt)",
								"data-variationbasewidth", ""+index_var_base_width,
								"data-variationbasecenter", ""+index_var_base_center,
								"data-variationsvgcenter", ""+index_var_svg_center,
								"fill", fillcolor,
								"data-normalfill", fillcolor
				});

				for (int k=0; k<q.ld_locs_starts.size(); ++k) {
					Integer spot_start = q.ld_locs_starts.get(k);
					Integer spot_end = q.ld_locs_ends.get(k);
					String is_coding = q.ld_coding.get(k);
					double var_svg_low = (double)((spot_start - chrom_low))*svg_units_per_base;
					double var_svg_high = (double)(((spot_end+1) - chrom_low))*svg_units_per_base;
					int var_base_width = (spot_end-spot_start)+1;
					double var_svg_center = 0.5*(var_svg_low+var_svg_high);
					double var_base_center = ((double)(spot_end+spot_start+1))*0.5;
					varname = "eqtlld_"+giv.index_variant_name+"_"+q.ld_variants.get(k)+"_e";
					width_in_pixels = (double)var_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
					double var_scaled_width_in_pixels = width_in_pixels;
					double var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
					if (var_scaled_width_in_pixels<variant_tick_min_pixel_width) {
						var_scaled_width_in_pixels = variant_tick_min_pixel_width;
						var_svg_startx = var_svg_center - 0.5*var_scaled_width_in_pixels;
						int ssi = (int)var_svg_startx;
						double ssifr = var_svg_startx - (double)ssi;
						if (ssifr>.5) var_svg_startx = (double)ssi+1.0;
						else var_svg_startx = (double)ssi;
					}
					ttt=q.ld_variants.get(k)+"#r2 = "+q.ld_r2.get(k)+"#pvalue = "+q.ld_pv.get(k)+"#beta = "+q.ld_beta.get(k);
					double min_r2 = .5;
					double what_r2 = Double.parseDouble(q.ld_r2.get(k));
					double tlength = 3.0 + 9.0*(what_r2-min_r2)/(1.0-min_r2);
					double ttop = 4.0 + (12.0 - tlength)/2.0;
					ttop = 20.0-tlength;
					String gcol = "#333333";
					if (is_coding.equals("1")) {
						gcol = "#FF0000";
					}
					ele = SvgGraphics.makeNode(d,
							group_for_gwas_hit,//parent
							"rect",//type
							new String[]{"x",""+var_svg_startx,
									"y",""+(current_top+ttop),
									"width",""+var_scaled_width_in_pixels,
									"height",""+tlength,
									"id", varname,
									"class", "eqtlvariant "+q.ld_variants.get(k),
									"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(q.ld_variants.get(k))+"')",
									"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt)+"')",
									"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(q.ld_variants.get(k))+"', evt)",
									"data-variationbasewidth", ""+var_base_width,
									"data-variationbasecenter", ""+var_base_center,
									"data-variationsvgcenter", ""+var_svg_center,
									"fill",gcol,
									"data-normalfill", gcol
					});
				}
				current_top+=20;
				varname = "svgeqtl_"+q.eqtl_table_row_id;
				ttt = "";
				String ttt_type = "0";
				if (!q.marker_equivalence_set_label.equals("None")) {
					ttt_type = "2";
					ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
					ttt+="<p>"+q.svg_display_name+"</p>";					
					ttt+="<p>Credible Set: "+q.marker_equivalence_set_label+"</p>";
					ttt+="<table id=\"aktmptable\" style=\"width:472px\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">Pvalue</th><th style=\"text-align: center;\" class=\"resizable-false\">Beta</th><th style=\"text-align: center;\" class=\"resizable-false\">r2</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding</th></tr></thead>";
					ttt+="<tbody>";
					boolean index_placed = false;
					for (int k=0; k<q.ld_locs_starts.size(); ++k) {
						Integer spot_start = q.ld_locs_starts.get(k);
						if (!index_placed) {
							if (k==0) {
								if (giv.index_variant_location_start<spot_start) {
									if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td>Y</td></tr>";
									else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td></td></tr>";
									index_placed = true;
								}
							}
						}
						if (q.ld_coding.get(k).equals("1")) ttt+="<tr><td>"+q.ld_variants.get(k)+"</td><td>"+String.format("%.2e", Double.valueOf(q.ld_pv.get(k)))+"</td><td>"+String.format("%.2f", Double.valueOf(q.ld_beta.get(k)))+"</td><td>"+String.format("%.3f", Double.valueOf(q.ld_r2.get(k)))+"</td><td>Y</td></tr>";
						else ttt+="<tr><td>"+q.ld_variants.get(k)+"</td><td>"+String.format("%.2e", Double.valueOf(q.ld_pv.get(k)))+"</td><td>"+String.format("%.2f", Double.valueOf(q.ld_beta.get(k)))+"</td><td>"+String.format("%.3f", Double.valueOf(q.ld_r2.get(k)))+"</td><td></td></tr>";
						if (!index_placed) {
							if (k==q.ld_locs_starts.size()-1) {
								if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td>Y</td></tr>";
								else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td></td></tr>";
								index_placed = true;
							} else if (giv.index_variant_location_start>=spot_start && giv.index_variant_location_start<q.ld_locs_starts.get(k+1)) {
								if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td>Y</td></tr>";
								else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td></td></tr>";
								index_placed = true;
							}
						}	    
					}
					if (!index_placed) {
						if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.3f", Double.valueOf(q.beta))+"</td><td></td><td>Y</td></tr>";
						else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td></td></tr>";
					}
					ttt+="</tbody>";
					ttt+="</table>";
					ttt+="</div>";
				} else {
					ttt_type = "2";
					ttt="<div id=\"aktmpdiv\" style=\"width: 900px; display: inline-block; font-size: 12px;\">";
					ttt+="<p>"+q.svg_display_name+"</p>";					
					ttt+="<p>Credible Set: "+q.marker_equivalence_set_label+"</p>";
					ttt+="<table id=\"aktmptable\" style=\"width:300px\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">Pvalue</th><th style=\"text-align: center;\" class=\"resizable-false\">Beta</th><th style=\"text-align: center;\" class=\"resizable-false\">r2</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding</th></tr></thead>";
					ttt+="<tbody>";
					if (giv.is_coding) ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.3f", Double.valueOf(q.beta))+"</td><td></td><td>Y</td></tr>";
					else ttt+="<tr><td>"+giv.index_variant_name+"</td><td>"+String.format("%.2e", Double.valueOf(q.pvalue))+"</td><td>"+String.format("%.2f", Double.valueOf(q.beta))+"</td><td></td><td></td></tr>";
					ttt+="</tbody>";
					ttt+="</table>";
					ttt+="</div>";
				}
				String what_weight = "400";
				ele = SvgGraphics.makeNode(d,
						group_for_gwas_hit,
						"text",//type
						new String[]{"x",""+index_var_svg_center,
								"y",""+(current_top+15),
								"id", varname,
								"font-weight",what_weight,
								"font-size","14",
								"fill", "#00739c",
								"class","akeqtltext",
								"data-origsvgx",""+index_var_svg_center,
								"font-style","italic",
								"cursor","default",
								"text-anchor","middle",
								"font-family","Lato, Sans-serif",
								"data-supl", ttt,
								"data-supltype", ttt_type,
								"onclick", "populatesupldiv2(evt)"
				});
				ele.setTextContent(q.svg_display_name);
				current_top+=20;
			}
		}
	}

	void LDGroupLayout(Vector<LDGroup> ldgroups, Document d, Element par, String svg_mode, String target_name) {

		int used = (gap_count+block_count)*square_size;
		int start_x_pos = ((int)((1000-used)/2));
		int cur_x_pos = start_x_pos;
		int current_top = (minus_footprints.size()+plus_footprints.size())*gene_line_height+kb_track_height;

		String vis = "visible";
		if (svg_mode.equals("Credible Sets")) vis = "hidden";

		Element group_for_ldg = SvgGraphics.makeNode(d,
				par,//parent
				"g",//type
				new String[]{"visibility", vis, "id", "group_for_ld"}
				);
		for (int i=0; i<ldgroups.size(); ++i) {
			cur_x_pos += square_size;
			LDGroup ldg = ldgroups.get(i);			
			String tt="<div id=\"aktmpdiv\" style=\"width: 930px; display: inline-block; font-size: 12px;\">";
			tt+="<table id=\"aktmptable\" style=\"width:921px;\"><thead><tr><th style=\"text-align: center;\" class=\"resizable-false\">Marker</th><th style=\"text-align: center;\" class=\"resizable-false\">Finding</th><th style=\"text-align: center;\" class=\"resizable-false\">Allele</th><th style=\"text-align: center;\" class=\"resizable-false\">Gene</th><th style=\"text-align: center;\" class=\"resizable-false\">Coding Impact</th><th style=\"text-align: center;\" class=\"resizable-false\">Phenotype</th><th style=\"text-align: center;\" class=\"resizable-false\">Tissue</th><th style=\"text-align: center;\" class=\"resizable-false\">Freq</th><th style=\"text-align: center;\" class=\"resizable-false\">OR/Beta</th></tr></thead>";
			tt+="<tbody>";
			for (int j=0; j<ldg.markers.size(); ++j) {
				LDGMarker lm = ldg.markers.get(j);
				for (int k=0; k<lm.coding_impacts.size(); ++k) {
					CodingVariantGeneAllele cv = lm.coding_impacts.get(k);	
					tt+="<tr><td>"+cv.name+"</td><td>C. Variant</td><td>"+cv.allele+"</td><td>"+cv.gene_symbol+"</td><td>"+cv.hgvs_protein+"</td><td></td><td></td><td>"+cv.gnomadgall_freq_str+" ExAC:ALL"+"</td><td></td></tr>";
				}
				for (int k=0; k<lm.gwas_results.size(); ++k) {
					GWASResult gwr = lm.gwas_results.get(k);
					String ob = "";
					if (!gwr.or_beta.equals("")) ob = String.format("%.2f", Double.valueOf(gwr.or_beta));
					tt+="<tr><td>"+lm.name+"</td><td>Assoc.</td><td>"+gwr.allele+"</td><td></td><td></td><td>"+gwr.trait+"</td><td></td><td>"+gwr.sum_1kgp3eur_maf_str+" sum(MAF) 1KGp3:EUR</td><td>"+ob+"</td></tr>";
				}
				for (int k=0; k<lm.eqtl_results.size(); ++k) {
					EQTLResult er = lm.eqtl_results.get(k);
					tt+="<tr><td>"+lm.name+"</td><td>eQTL</td><td>"+er.effect_allele+"</td><td>"+er.gene_symbol+"</td><td></td><td></td><td>"+er.tissue+"</td><td>"+er.sum_1kgp3eur_maf_str+" sum(MAF) 1KGp3:EUR</td><td>"+String.format("%.2f", Double.valueOf(er.beta))+"</td></tr>";
				}
			}			
			tt+="</tbody>";
			tt+="</table>";
			tt+="</div>";
			String classname = "ldgroup"+i;
			String ldg_ids = "";
			for (int j=0; j<ldg.markers.size(); ++j) {
				LDGMarker lm = ldg.markers.get(j);
				for (int k=0; k<lm.coding_impacts.size(); ++k) {
					CodingVariantGeneAllele cv = lm.coding_impacts.get(k);	
					ldg_ids+="CV"+cv.db_id+":"+lm.id+"-";
				}
				for (int k=0; k<lm.gwas_results.size(); ++k) {
					GWASResult gwr = lm.gwas_results.get(k);
					ldg_ids+="GR"+gwr.gwas_table_row_id+"-";
				}
				for (int k=0; k<lm.eqtl_results.size(); ++k) {
					EQTLResult er = lm.eqtl_results.get(k);
					ldg_ids+="ER"+er.eqtl_table_row_id+"-";
				}
			}			
			for (int j=0; j<ldg.markers.size(); ++j) {
				LDGMarker lm = ldg.markers.get(j);
				double lm_var_svg_low = (double)((lm.start_1based - chrom_low))*svg_units_per_base;
				int end_1based = lm.start_1based; // quick, for now .. can refine later for multi-base events
				double lm_var_svg_high = (double)(((end_1based+1) - chrom_low))*svg_units_per_base;
				// below line will cause problems for insertions, which could have a net width of <1 .. FIX!
				int lm_var_base_width = (end_1based-lm.start_1based)+1;
				if (lm_var_base_width<1) lm_var_base_width = 1;
				double lm_var_svg_center = 0.5*(lm_var_svg_low+lm_var_svg_high);
				double lm_var_base_center = ((double)(end_1based+lm.start_1based+1))*0.5;
				String varname = "ldg"+lm.name;
				double width_in_pixels = (double)lm_var_base_width*svg_units_per_base; // this applies to unzoomed, therefore svg=pixel
				double scaled_width_in_pixels = width_in_pixels;
				double svg_startx = lm_var_svg_center - 0.5*scaled_width_in_pixels;
				if (scaled_width_in_pixels<variant_tick_min_pixel_width) {
					scaled_width_in_pixels = variant_tick_min_pixel_width;
					svg_startx = lm_var_svg_center - 0.5*scaled_width_in_pixels;
					int ssi = (int)svg_startx;
					double ssifr = svg_startx - (double)ssi;
					if (ssifr>.5) svg_startx = (double)ssi+1.0;
					else svg_startx = (double)ssi;
				}
				String fillcolor = "#333333";
				Element ele = SvgGraphics.makeNode(d,
						group_for_ldg,//parent
						"rect",//type
						new String[]{"x",""+svg_startx,
								"y",""+(current_top),
								"width",""+scaled_width_in_pixels,
								"height",""+ld_marker_tick_size,
								"id", varname+" ldg",
								"class", "ldgbar ldgbar_"+lm.name,
								"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(lm.name)+"')",
								"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(lm.name)+"')",
								"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(lm.name)+"', evt)",
								"data-variationbasewidth", ""+lm_var_base_width,
								"data-variationbasecenter", ""+lm_var_base_center,
								"data-variationsvgcenter", ""+lm_var_svg_center,
								"fill", fillcolor,
								"data-normalfill", fillcolor
				});
				int cur_y_top = current_top + box_start_y_offset;
				double half_ss = square_size/2.0;
				for (int k=0; k<lm.coding_impacts.size(); ++k) {
					CodingVariantGeneAllele cv = lm.coding_impacts.get(k);	
					String col = "#444";
					if (cv.gene_symbol.equals(target_name)) col = "#c60071";
					String tt2=lm.name+"#"+cv.allele+"#"+cv.gene_symbol+"#"+cv.hgvs_protein;
					Element agroup = SvgGraphics.makeNode(d,
							group_for_ldg,//parent
							"g",//type
							new String[]{
									"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(tt2)+"')",
									"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(tt2)+"')",
									"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(tt2)+"', evt)",
									"data-supl", tt,
									"data-supltype", "3",
									"data-class", classname,
									"data-ldgids", ldg_ids,
									"id","CV"+cv.db_id+":"+lm.id,
									"class", classname+" m"+lm.id,
									"onclick", "populatesupldiv(evt)"
					});
					Element ele2 = SvgGraphics.makeNode(d,
							agroup,//parent
							"rect",//type
							new String[]{"x",""+cur_x_pos,
									"y",""+(cur_y_top),
									"width",""+square_size,
									"height",""+square_size,
									"stroke", fillcolor,
									"stroke-width", "1",
									"fill", "#e0e0e0"
					});
					ele2 = SvgGraphics.makeNode(d,
							agroup,
							"text",//type
							new String[]{"x",""+(cur_x_pos+half_ss),
									"y",""+(cur_y_top+half_ss),
									"font-weight","400",
									"font-size","20",
									"fill", col,
									"class","akldgtext",
									//"data-origsvgx",""+(cur_x_pos+half_ss),
									//"data-origsvgy",""+(cur_y_top+half_ss),
									"data-squaresize",""+(square_size),
									"cursor","default",
									"text-anchor","middle",
									"dominant-baseline", "central",
									"font-family","Lato, Sans-serif"
					});
					ele2.setTextContent("C");
					cur_y_top+=square_size;
				}
				for (int k=0; k<lm.gwas_results.size(); ++k) {
					GWASResult gwr = lm.gwas_results.get(k);
					String ttt2 = lm.name+"#"+gwr.svg_display_name;
					Element agroup = SvgGraphics.makeNode(d,
							group_for_ldg,//parent
							"g",//type
							new String[]{
									"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(ttt2)+"')",
									"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt2)+"')",
									"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(ttt2)+"', evt)",
									"data-supl", tt,
									"data-supltype", "3",
									"data-class", classname,
									"data-ldgids", ldg_ids,
									"id","GR"+gwr.gwas_table_row_id,
									"class", classname,
									"onclick", "populatesupldiv(evt)"
					});
					Element ele2 = SvgGraphics.makeNode(d,
							agroup,//parent
							"rect",//type
							new String[]{"x",""+cur_x_pos,
									"y",""+(cur_y_top),
									"width",""+square_size,
									"height",""+square_size,
									"stroke", fillcolor,
									"stroke-width", "1",
									"fill", "#e0e0e0"
					});
					ele2 = SvgGraphics.makeNode(d,
							agroup,
							"text",//type
							new String[]{"x",""+(cur_x_pos+half_ss),
									"y",""+(cur_y_top+half_ss),
									"font-weight","400",
									"font-size","20",
									"fill", "#444",
									"class","akldgtext",
									"data-squaresize",""+(square_size),
									"cursor","default",
									"text-anchor","middle",
									"dominant-baseline", "central",
									"font-family","Lato, Sans-serif"
					});
					if (!gwr.is_pqtl) ele2.setTextContent("A");
					else ele2.setTextContent("P");
					cur_y_top+=square_size;
				}
				for (int k=0; k<lm.eqtl_results.size(); ++k) {
					EQTLResult er = lm.eqtl_results.get(k);
					String col = "#444";
					if (er.gene_symbol.equals(target_name)) col = "#c60071";
					String ttt2 = lm.name+"#"+er.svg_display_name;
					Element agroup = SvgGraphics.makeNode(d,
							group_for_ldg,//parent
							"g",//type
							new String[]{
									"onmouseover", "highlightvariants('"+QuoteEscape.quote_escape(ttt2)+"')",
									"onmousemove", "ShowTooltip(evt,'"+QuoteEscape.quote_escape(ttt2)+"')",
									"onmouseout", "hideanddehighlight('"+QuoteEscape.quote_escape(ttt2)+"', evt)",
									"data-supl", tt,
									"id","ER"+er.eqtl_table_row_id,
									"data-supltype", "3",
									"data-class", classname,
									"data-ldgids", ldg_ids,
									"class", classname,
									"onclick", "populatesupldiv(evt)"
					});
					Element ele2 = SvgGraphics.makeNode(d,
							agroup,//parent
							"rect",//type
							new String[]{"x",""+cur_x_pos,
									"y",""+(cur_y_top),
									"width",""+square_size,
									"height",""+square_size,
									"stroke", fillcolor,
									"stroke-width", "1",
									"fill", "#e0e0e0"
					});
					ele2 = SvgGraphics.makeNode(d,
							agroup,
							"text",//type
							new String[]{"x",""+(cur_x_pos+half_ss),
									"y",""+(cur_y_top+half_ss),
									"font-weight","400",
									"font-size","20",
									"fill", col,
									"class","akldgtext",
									"data-squaresize",""+(square_size),
									"cursor","default",
									"text-anchor","middle",
									"dominant-baseline", "central",
									"font-family","Lato, Sans-serif"
					});
					ele2.setTextContent("E");
					cur_y_top+=square_size;
				}
				if (lm.ld_data_likely_exist) {				
					Element ele3 = SvgGraphics.makeNode(d,
							group_for_ldg,//parent
							"line",//type
							new String[]{"x1",""+(cur_x_pos+(int)(square_size/2)),
									"y1",""+(current_top+box_start_y_offset-2),
									"x2",""+(svg_startx+(int)(scaled_width_in_pixels/2)),
									"y2",""+(current_top+2+ld_marker_tick_size),
									"stroke", fillcolor,
									"stroke-width", "1"
					});
				} else {
					Element ele3 = SvgGraphics.makeNode(d,
							group_for_ldg,//parent
							"line",//type
							new String[]{"x1",""+(cur_x_pos+(int)(square_size/2)),
									"y1",""+(current_top+box_start_y_offset-2),
									"x2",""+(svg_startx+(int)(scaled_width_in_pixels/2)),
									"y2",""+(current_top+2+ld_marker_tick_size),
									"stroke", fillcolor,
									"stroke-width", "1",
									"stroke-dasharray", "5, 10"
					});
				}
				cur_x_pos+=square_size;				
			}
		}
	}

	void TranscriptLayout(GeneAnnotation ga, Document d, Element par) {	
		Integer tx_start = ga.exon_starts.get(0);
		Integer tx_end = ga.exon_ends.get(ga.num_exons-1);
		/*
		  *_mid values prevent pixel aliasing problem if we use the exon-terminal coordinates
		 */
		Integer tx_start_mid = (ga.exon_starts.get(0)+ga.exon_ends.get(0))/2;
		Integer tx_end_mid = (ga.exon_starts.get(ga.num_exons-1)+ga.exon_ends.get(ga.num_exons-1))/2;
		if (tx_start>tx_end) {
			tx_start = ga.exon_starts.get(ga.num_exons-1);
			tx_end = ga.exon_ends.get(0);
			tx_end_mid = (ga.exon_starts.get(0)+ga.exon_ends.get(0))/2;
			tx_start_mid = (ga.exon_starts.get(ga.num_exons-1)+ga.exon_ends.get(ga.num_exons-1))/2;
		}
		int gene_top = 0;
		if (ga.is_forward_strand==1) {
			gene_top = first_plus_strand_gene_top - gene_line_height*(ga.which_gene_line-1);
		} else {
			gene_top = first_minus_strand_gene_top + gene_line_height*(ga.which_gene_line-1);
		}
		double tx_start_svg = (double)((tx_start - chrom_low))*svg_units_per_base;
		if (tx_start_svg<0.0) tx_start_svg = 0.0;
		double tx_end_svg = (double)(((tx_end+1) - chrom_low))*svg_units_per_base;
		if (tx_end_svg>(double)svg_width) tx_end_svg = (double)svg_width;
		if (tx_start_svg>(double)svg_width) {return;}
		if (tx_end_svg<(double)0.0) {return;}
		double tx_start_mid_svg = (double)((tx_start_mid - chrom_low))*svg_units_per_base;
		if (tx_start_mid_svg<0.0) tx_start_mid_svg = 0.0;
		double tx_end_mid_svg = (double)(((tx_end_mid+1) - chrom_low))*svg_units_per_base;
		if (tx_end_mid_svg>(double)svg_width) tx_end_mid_svg = (double)svg_width;
		double tx_span_mid_svg = tx_end_mid_svg - tx_start_mid_svg;
		int tx_start_svg_bounding_int = (int)tx_start_svg;
		int tx_end_svg_bounding_int = (int)(tx_end_svg+1.0);
		int tx_span_svg_bounding_width = tx_end_svg_bounding_int - tx_start_svg_bounding_int;
		double tx_center_svg = (tx_end_svg+tx_start_svg)/2.0;
		if (ga.is_gene_target==1) {
			alt_viewbox_fraction_of_full_field = (double)tx_span_svg_bounding_width/(double)svg_width;
			alt_viewbox_svg_x = tx_start_svg_bounding_int;
			alt_viewbox_svg_width = tx_span_svg_bounding_width;
			alt_viewbox_base_start = (double)chrom_low+(double)alt_viewbox_svg_x/svg_units_per_base;
			alt_viewbox_base_width = (double)width_in_bases*(double)alt_viewbox_svg_width/(double)svg_width;
			alt_viewbox_pixels_per_base = (double)svg_width/alt_viewbox_base_width;
			alt_viewbox_click_varname = ga.gene_id+"cfull";
		}
		String varname = ga.gene_id+"full";
		String col = "#111111";
		if (ga.is_gene_target==1) col = "#c60071";
		Element group_for_a_tx = SvgGraphics.makeNode(d,
				par,
				"g",
				null
				);
		Element ele = SvgGraphics.makeNode(d,
				group_for_a_tx,
				"rect",
				new String[]{"x",""+tx_start_mid_svg,
						"y",""+(transcript_bar_from_top+gene_top),
						"width",""+tx_span_mid_svg,
						"height",""+transcript_bar_height,
						"id", varname,
						"fill",col
		});
		varname = ga.gene_id+"text";
		String gtext = ga.symbol+" >";
		if (ga.is_forward_strand!=1) {
			gtext = "< "+ga.symbol;
		}
		ele = SvgGraphics.makeNode(d,
				group_for_a_tx,
				"text",
				new String[]{"x",""+tx_center_svg,
						"y",""+(gene_name_from_top+gene_top),
						"id", varname,
						"font-weight","800",
						"class","akgenename",
						"data-origsvgx",""+tx_center_svg,
						"font-size","14",
						"cursor","default",
						"text-anchor","middle",
						"font-family","Lato, Sans-serif"
		});
		ele.setTextContent(gtext);
		for (int i=0; i<ga.num_exons; ++i) {
			int ec = i+1;
			Integer ex_start = ga.exon_starts.get(i);
			Integer ex_end = ga.exon_ends.get(i);
			if (ex_end<chrom_low) continue;
			if (ex_start>chrom_high) continue;
			double ex_start_svg = (double)((ex_start - chrom_low))*svg_units_per_base;
			if (ex_start_svg<0.0) ex_start_svg = 0.0;
			double ex_end_svg = (double)(((ex_end+1) - chrom_low))*svg_units_per_base;
			if (ex_end_svg>(double)svg_width) ex_end_svg = (double)svg_width;
			double ex_span_svg = ex_end_svg - ex_start_svg;
			varname = ga.gene_id+"ex"+ec;
			ele = SvgGraphics.makeNode(d,
					group_for_a_tx,
					"rect",
					new String[]{"x",""+ex_start_svg,
							"y",""+(fat_exon_from_top+gene_top),
							"width",""+ex_span_svg,
							"height",""+fat_exon_height,
							"id", varname,
							"fill",col
			});
		}
		if (ga.coding_start!=-1) {
			if (ga.coding_end>=chrom_low && ga.coding_start<=chrom_high) {	    
				double c_start_svg = (double)((ga.coding_start - chrom_low))*svg_units_per_base;
				if (c_start_svg<0.0) c_start_svg = 0.0;
				double c_end_svg = (double)(((ga.coding_end+1) - chrom_low))*svg_units_per_base;
				if (c_end_svg>(double)svg_width) c_end_svg = (double)svg_width;
				double c_span_svg = c_end_svg - c_start_svg;
				varname = ga.gene_id+"cfull";
				if (ga.is_gene_target!=1) {
					ele = SvgGraphics.makeNode(d,
							group_for_a_tx,
							"rect",
							new String[]{"x",""+c_start_svg,
									"y",""+(cds_bar_from_top+gene_top),
									"width",""+c_span_svg,
									"height",""+cds_bar_height,
									"id", varname,
									"fill","#aaaaaa"
					});
				} else {
					ele = SvgGraphics.makeNode(d,
							group_for_a_tx,
							"rect",
							new String[]{"x",""+c_start_svg,
									"y",""+(cds_bar_from_top+gene_top),
									"width",""+c_span_svg,
									"height",""+cds_bar_height,
									"id", varname,
									"fill","#aaaaaa"
					});   
				}
			}
		}
	}

	Layout() {}

}



