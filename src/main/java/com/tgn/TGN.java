package com.tgn;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

import static spark.Spark.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import com.google.gson.*;

import org.w3c.dom.*;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import javax.xml.bind.DatatypeConverter;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.math.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.*;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

// mvn clean package
// java -jar target/tgn-1.0-TGN.jar ~/TargetGeneNotebook/ 4567

class Detail {

	String id = "";
	String b64_contents = "";
	String description = "";
	String section_assignment = "";

	Detail() {}

}

class GenePhenAssoc {
	
	String db_id = "";
	String gene = "";
	String phenotype = "";
	String source = "";
	String linkout = "";
	String curator_comment = "";
	
	GenePhenAssoc() {}
	
}

class PhenAlleleAssoc {
	
	String gene_string = "";
	String phenotype = "";
	String source = "";
	String linkout = "";
	String clinical_significance = "";
	String unaudited_risk_allele = "";
	String variant = "";
	
	PhenAlleleAssoc() {}
	
}

class PubmedDocument {

	String journal = "";
	String first_author = "";
	String title = "";
	String pubmed_id = "";
	String pub_year = "";
	String id = "";

	PubmedDocument() {}

}

class BiorxivDocument {

	String publisher = "";
	String first_author = "";
	String title = "";
	String doi_id = "";
	String pub_year = "";
	String pub_date = "";
	String id = "";

	BiorxivDocument() {}

}

class FileDocument {

	String year = "";
	String name = "";
	String description = "";
	String contents = "";
	String id = "";

	FileDocument() {}

}

class WebDocument {

	String year = "";
	String site = "";
	String description = "";
	String id = "";

	WebDocument() {}

}

class SourceDocument {

	PubmedDocument pmdoc = null;
	FileDocument fdoc = null;
	WebDocument wdoc = null;
	BiorxivDocument bdoc = null;
	Vector<Detail> details = new Vector<>();
	String id = "";
	boolean automatic = false;
	String outbound_link = "";
	int has_been_reviewed = 0;
	String curator_comment ="";

	SourceDocument() {}

}

class GeneAnnotation {

	String symbol = "";
	String gene_id = "";
	String db_id = "";
	String transcript_id = "";
	int coding_start = -1;
	int coding_end = -1;
	Vector<Integer> exon_starts = new Vector<>();
	Vector<Integer> exon_ends = new Vector<>();
	int num_exons = -1;
	int is_forward_strand = -1;
	int is_gene_target = -1;
	int which_gene_line = -1;

	GeneAnnotation() {}

}

class GWASResult {

	String column_display_text = "";
	String outbound_link = "";
	int document_year = -1;
	String or_beta = "";
	String pvalue = "";
	String trait = "";
	int gwas_table_row_id = -1;
	String curator_comment = "";
	String marker_equivalence_set = "";
	boolean show_in_svg = false;
	String index_variant_name = "";
	int index_variant_start = -1;
	int index_variant_end = -1;
	boolean typed_in_accepted_dataset = false;
	boolean automatic = false;
	Vector<Integer> mes_locs_starts = new Vector<>();
	Vector<Integer> mes_locs_ends = new Vector<>();
	Vector<String> mes_variants = new Vector<>();
	Vector<String> mes_coding = new Vector<>();
	Vector<String> mes_r2 = new Vector<>();
	Vector<String> mes_posterior = new Vector<>();
	Vector<String> LDdatasets = new Vector<>();
	Vector<String> LDdatasetsHighR2Count = new Vector<>();
	String svg_display_name = "";
	String allele="";
	String allele_sup="";
	String sum_1kgp3eur_maf_str = "n/a";
	String credible_set_name = "";
	double credible_set_posterior = -1.0;
	int credible_set_member_count = 0;
	boolean is_pqtl = false;
	
	GWASResult() {}

}

class EQTLResult {

	String column_display_text = "";
	String outbound_link = "";
	int document_year = -1;
	String beta = "";
	String pvalue = "";
	String gene_symbol = "";
	String tissue = "";
	String effect_allele="";
	int eqtl_table_row_id = -1;
	String curator_comment = "";	
	boolean show_in_svg = false;
	String eqtl_marker_equivalence_set_id = "";
	String marker_equivalence_set_label = "";
	String marker_equivalence_set_count_label = "";
	String index_variant_name = "";
	int index_variant_start = -1;
	int index_variant_end = -1;
	boolean automatic = false;
	Vector<Integer> ld_locs_starts = new Vector<>();
	Vector<Integer> ld_locs_ends = new Vector<>();
	Vector<String> ld_variants = new Vector<>();
	Vector<String> ld_coding = new Vector<>();
	Vector<String> ld_r2 = new Vector<>();
	Vector<String> ld_pv = new Vector<>();
	Vector<String> ld_beta = new Vector<>();
	String svg_display_name = "";
	String sum_1kgp3eur_maf_str = "n/a";
	int association_overlap_count = 0;

	EQTLResult() {}

}

class LDGroup {
	
	double avg_start_1based = -1.0;
	int left_edge_svg_location = -1;
	Vector<LDGMarker> markers = new Vector<>();

	LDGroup() {}

}

class IndexVariant {

	String index_variant_name = "";
	int index_variant_location_start = -1;
	int index_variant_location_end = -1;
	boolean is_coding = false;
	Vector<GWASResult> gwas_results = new Vector<>();
	Vector<EQTLResult> eqtl_results = new Vector<>();

	IndexVariant() {}

}

class CodingVariantGeneAllele {
	
	String db_id = "";
	String gene_symbol = "";
	String name = "";
	int start_1based = -1;
	String allele = "";
	String sift = "";
	String polyphen = "";
	String so_terms = "";
	String display_terms = "";
	String ranks = "";
	String impact_terms = "";
	String codon_allele_string = "";
	String hgvs_protein = "";
	String hgvs_genomic = "";
	double max_gnomadg_subpop_freq = -1.0;
	String max_gnomadg_subpop_freq_str = "";
	String gnomadgall_freq_str = "n/a";
	int vma_id = -1;	
	int vm_id = -1;
	Hashtable<String, String> frequencies = new Hashtable<>();
	boolean show_in_ld_groups = false;

	CodingVariantGeneAllele() {}

}

class LDGMarker {

	Integer id = Integer.valueOf(-1);
	String name = "";
	int start_1based = -1;
	Vector<CodingVariantGeneAllele> coding_impacts = new Vector<>();
	Vector<GWASResult> gwas_results = new Vector<>();
	Vector<EQTLResult> eqtl_results = new Vector<>();
	boolean ld_data_likely_exist = false;

	LDGMarker() {}

}

class UpdateGWASCommentPostMsg {

	String todo="";
	String gwas_comment="";
	int gwas_db_id = -1;

	UpdateGWASCommentPostMsg() {}

}

class UpdateGenePhenCommentPostMsg {

	String todo="";
	String genephen_comment="";
	int genephen_db_id = -1;

	UpdateGenePhenCommentPostMsg() {}

}

class DeleteCredibleSetPostMsg {

	String todo="";
	int gwas_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	DeleteCredibleSetPostMsg() {}

}

class UpdateDetailCommentPostMsg {

	String todo="";
	String comment="";
	int detail_db_id = -1;

	UpdateDetailCommentPostMsg() {}

}

class UpdateEQTLCommentPostMsg {

	String todo="";
	String eqtl_comment="";
	int eqtl_db_id = -1;

	UpdateEQTLCommentPostMsg() {}

}

class UpdateSourceCommentPostMsg {

	String todo="";
	String source_comment="";
	int source_db_id = -1;

	UpdateSourceCommentPostMsg() {}

}

class DownloadFilePostMsg {

	String todo="";
	int sd_db_id = -1;

	DownloadFilePostMsg() {}

}

class OkToRemoveRefPostMsg {

	String todo="";
	int sd_db_id = -1;

	OkToRemoveRefPostMsg() {}

}

class SubmitNewGWASRowPostMsg {

	String todo="";
	String phenotype="";
	String source_db_id="";
	String index_variant="";
	String allele="";
	String pvalue="";
	String or_beta="";
	String comment="";
	String is_pqtl="";

	SubmitNewGWASRowPostMsg() {}

}

class SubmitNewEQTLRowPostMsg {

	String todo="";
	String db_source_id="";
	String gene_symbol="";
	String tissue="";
	String indexvariant="";
	String pvalue="";
	String beta="";
	String allele="";
	String comment="";

	SubmitNewEQTLRowPostMsg() {}

}

class SubmitNewDetailPostMsg {

	String todo="";
	String source_db_id="";
	String desc="";
	String img_b64="";

	SubmitNewDetailPostMsg() {}

}

class CreateNewCredibleSetPostMsg {
	
	String todo="";
	String gwas_db_id="";
	String credible_set_name="";
	String index_variant_posterior="";
	ArrayList<String> member_rs_numbers;
	ArrayList<String> member_posteriors;
	
	CreateNewCredibleSetPostMsg() {}
	
}

class SubmitNewPubmedPostMsg {

	String todo="";
	String pubmed_id="";

	SubmitNewPubmedPostMsg() {}

}

class SubmitNewBiorxivPostMsg {

	String todo="";
	String doi="";

	SubmitNewBiorxivPostMsg() {}

}

class SubmitDeleteTagPostMsg {

	String todo="";
	String tag_id="";

	SubmitDeleteTagPostMsg() {}

}

class SubmitSaveTagPostMsg {
	
	String todo="";
	String tag_id="";
	String tag_class_id="";
	String short_name="";
	String long_name="";
	String description="";
	String color="";
	
	SubmitSaveTagPostMsg() {}
	
}

class SubmitNewFilePostMsg {

	String todo="";
	String file_year = "";
	String file_name = "";
	String file_description = "";
	String file_contents = "";

	SubmitNewFilePostMsg() {}

}

class SubmitNewWebPostMsg {

	String todo="";
	String web_year = "";
	String web_site = "";
	String web_description = "";

	SubmitNewWebPostMsg() {}

}

class SubmitPushNewWebPostMsg {

	String todo="";
	String push_year = "";
	String push_site = "";
	String push_description = "";
	ArrayList<String> push_dbs = new ArrayList<>();;
	
	SubmitPushNewWebPostMsg() {}

}

class SubmitTagAssignPostMsg {
	
	String todo="";
	String tag_id="";
	String db="";
	String addif1="";
	
	SubmitTagAssignPostMsg() {}
	
}

class SubmitSectionAssignmentPostMsg {

	String todo="";
	String detail_id="";
	String new_section="";

	SubmitSectionAssignmentPostMsg() {}

}

class UpdateGWASSVGDisplayNamePostMsg {

	String todo="";
	String svgdisplayname="";
	int gwas_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateGWASSVGDisplayNamePostMsg() {}

}

class UpdateEQTLSVGDisplayNamePostMsg {

	String todo="";
	String svgdisplayname="";
	int eqtl_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateEQTLSVGDisplayNamePostMsg() {}

}

class RemoveDetailPostMsg {

	String todo="";
	String detail_id="";

	RemoveDetailPostMsg() {}

}

class UpdateGWASMarkerEquivalenceSetPostMsg {

	String todo="";
	String marker_equivalence_set="";
	int gwas_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateGWASMarkerEquivalenceSetPostMsg() {}

}

class RemoveGWASRowPostMsg {

	String todo="";
	String gwas_db_id = "";
	String svg_display_mode="";
	int hidenoncoding=-1;

	RemoveGWASRowPostMsg() {}

}

class RemoveEQTLRowPostMsg {

	String todo="";
	String eqtl_db_id = "";
	String svg_display_mode="";
	int hidenoncoding=-1;

	RemoveEQTLRowPostMsg() {}

}

class UpdateGWASShowHidePostMsg {

	String todo="";
	String show_or_hide = "";
	int gwas_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateGWASShowHidePostMsg() {}

}

class UpdateSourceReviewPostMsg {

	String todo="";
	String reviewed_or_not = "";
	int source_db_id = -1;

	UpdateSourceReviewPostMsg() {}

}

class UpdateShowHideNonCodingPostMsg {

	String todo="";
	int hidenoncoding=-1;
	String svg_display_mode="";

	UpdateShowHideNonCodingPostMsg() {}

}

class UpdateMarkerForLDPostMsg {

	String todo="";
	String add_or_remove = "";
	int vm_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateMarkerForLDPostMsg() {}

}

class UpdateEQTLShowHidePostMsg {

	String todo="";
	String show_or_hide = "";
	int eqtl_db_id = -1;
	String svg_display_mode="";
	int hidenoncoding=-1;

	UpdateEQTLShowHidePostMsg() {}

}

class UpdateSummaryPostMsg {

	String todo="";
	String summary="";

	UpdateSummaryPostMsg() {}

}

class OutgoingPostMsg {

	String sentcontents="";
	String isok="";

	OutgoingPostMsg() {}

}

class OutgoingOverlapPostMsg {

	String sentcontents="";
	String isok="";
	String eqtl_ids = "";
	String overlap_counts = "";

	OutgoingOverlapPostMsg() {}

}

class OutgoingSourceReviewPostMsg {

	String sentcontents="";
	String isok="";
	String unreviewed_count="";
	String total_count="";

	OutgoingSourceReviewPostMsg() {}

}

class OutgoingDownloadFilePostMsg {

	String isok="";
	String name="";
	String b64="";

	OutgoingDownloadFilePostMsg() {}

}

class OutgoingPubAndGeneListMsg {
	
	String isok = "";
	String pub_list = "";
	String gene_list = "";
	
	OutgoingPubAndGeneListMsg() {}

}

class OutgoingTagClassListMsg {

	String isok="";
	String class_list="";
	
	OutgoingTagClassListMsg() {}
	
}

class OutgoingTagAssignsMsg {
	
	String isok="";
	String db_tag_assigns="";
	
	OutgoingTagAssignsMsg() {}

}

class OutgoingDBNameListMsg {
	
	String isok="";
	String db_name_list="";
	
	OutgoingDBNameListMsg() {}

}


class OutgoingPubListMsg {
	
	String isok = "";
	String pub_list = "";
	
	OutgoingPubListMsg() {}

}

class OutgoingSaveTagPostMsg {
	
	String isok = "";
	String errors = "";
	String trtext = "";
	String tagid = "";

	OutgoingSaveTagPostMsg() {}
	
}

class OutgoingNewGWASRowPostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String gwasdbid="";

	OutgoingNewGWASRowPostMsg() {}

}

class OutgoingNewEQTLRowPostMsg {

	String isok="";
	String errors="";
	String newrow="";
	String eqtldbid="";

	OutgoingNewEQTLRowPostMsg() {}

}

class OutgoingOkToRemoveRefPostMsg {

	String isok="";
	String problems="";
	String unreviewed_count="";
	String total_count="";

	OutgoingOkToRemoveRefPostMsg() {}

}

class OutgoingNewDetailPostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String detaildbid="";

	OutgoingNewDetailPostMsg() {}

}

class OutgoingSectionAssignmentPostMsg {

	String isok="";
	String old_section_lc="";
	String old_section_source_documents_id="";
	String old_section_revised_rows="";
	String new_section_lc="";
	String new_section_source_documents_id="";
	String new_section_revised_rows="";	

	OutgoingSectionAssignmentPostMsg() {}

}

class OutgoingRemoveDetailPostMsg {

	String isok="";
	String old_section_lc="";
	String old_section_source_documents_id="";
	String old_section_revised_rows="";
	String source_documents_id="";

	OutgoingRemoveDetailPostMsg() {}

}

class OutgoingNewPubmedPostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String trtext2="";
	String sourcedocumentsid="";
	String unreviewed_count="";
	String total_count="";

	OutgoingNewPubmedPostMsg() {}

}

class OutgoingNewCredibleSetPostMsg {

	String isok="";
	String errors="";
	String csname="";

	OutgoingNewCredibleSetPostMsg() {}

}


class OutgoingPushNewWebPostMsg {

	String isok="";
	String messages="";
	
	OutgoingPushNewWebPostMsg() {}
	
}

class OutgoingUnreviewedCheckinPostMsg {
	
	String isok="";
	String gene_list="";
	
	OutgoingUnreviewedCheckinPostMsg() {}
	
}

class OutgoingNewBiorxivPostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String trtext2="";
	String sourcedocumentsid="";
	String unreviewed_count="";
	String total_count="";

	OutgoingNewBiorxivPostMsg() {}

}

class OutgoingNewFilePostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String trtext2="";
	String sourcedocumentsid="";
	String unreviewed_count="";
	String total_count="";

	OutgoingNewFilePostMsg() {}

}

class OutgoingNewWebPostMsg {

	String isok="";
	String errors="";
	String trtext="";
	String trtext2="";
	String sourcedocumentsid="";
	String unreviewed_count="";
	String total_count="";

	OutgoingNewWebPostMsg() {}

}

public class TGN {

	static String publicFileFolder;
	static String databaseFolder;
	static Hashtable<String, Boolean> gene_synchronize_objects = new Hashtable<>();
	
	public static void main(String[] args) {
		int port = 4567;
		if (args.length <1){
			System.err.println("provide data directory and port - /home/user/data   4567");
		}
		if (args.length >1){
			port = Integer.parseInt(args[1]);
		}

		String storage_dir = args[0];
		//storage_dir is the command  argument. db files will be in this directory
		if (!storage_dir.endsWith(File.separator)) storage_dir+=File.separator;
		final String sd = new String(storage_dir);

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}

		spark.Spark.setPort(port);
		
		databaseFolder = storage_dir;
		staticFileLocation("/public");
		/*
		 * public/ should be found in project's src/main/resources/ folder
		 * 
		 * served files should then be referenced relative to public/
		 * so src/main/resources/public/index.html would just be referred
		 * to as /index.html
		 */

		final boolean dump_log = true;
		
		get(new Route("/favicon.ico") {
			@Override
			public Object handle(Request request, Response response) {
				return "";
			}
		});
		
		get(new Route("/index.html") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try { 
					String s = GetDBList(sd);
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		
		get(new Route("/_tagdb_") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = TagCreatePage(sd);
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;					
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		
		get(new Route("/unreviewedcheckin") {
			@Override
			public Object handle(Request request, Response response) {		
				if (dump_log) System.out.println("REQUEST: "+request.body());
				String isok = "notok";
				String ret = "";
				Gson gson = new Gson();
				try {
					ret = UnreviewedCheckin(sd);
					isok = "ok";
				} catch (Exception ee) {}
				OutgoingUnreviewedCheckinPostMsg oucpm = new OutgoingUnreviewedCheckinPostMsg();
				oucpm.isok = isok;
				if (isok.equals("ok")) {
					oucpm.gene_list = ret;
				}
				String s = gson.toJson(oucpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;	
			}
		});
		
		get(new Route("/") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = GetDBList(sd);
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		/*
		get(new Route("/contact.html") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = GetContact();
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		
		get(new Route("/faq.html") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = GetFAQ();
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		
		get(new Route("/terms.html") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = GetTerms();
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		
		get(new Route("/about.html") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				try {
					String s = GetAbout();
					if (dump_log) System.out.println("RESPONSE: "+s);
					return s;
				} catch (Exception ee) {
					ee.printStackTrace();
					return "Unable to load page";
				}
			}
		});
		*/
		get(new Route("/get_tag_class_list"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				Gson gson = new Gson();
				if (!gene_synchronize_objects.containsKey("__tags__")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("__tags__", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("__tags__");
				OutgoingTagClassListMsg opm = new OutgoingTagClassListMsg();
				opm.isok = "notok";
				synchronized(sy) {
					try {
						opm.class_list = GetTagClassList(sd);
						opm.isok = "ok";
					} catch (Exception ee) {
					}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	
		
		get(new Route("/get_db_tag_assigns"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				Gson gson = new Gson();
				if (!gene_synchronize_objects.containsKey("__tags__")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("__tags__", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("__tags__");
				OutgoingTagAssignsMsg otam = new OutgoingTagAssignsMsg();
				otam.isok = "notok";
				synchronized(sy) {
					try {
						otam.db_tag_assigns = GetDBTagList(sd);
						otam.isok = "ok";
					} catch (Exception ee) {
					}
				}
				String s = gson.toJson(otam);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	
		
		get(new Route("/get_all_db_list"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				Gson gson = new Gson();
				if (!gene_synchronize_objects.containsKey("__tags__")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("__tags__", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("__tags__");
				OutgoingDBNameListMsg otam = new OutgoingDBNameListMsg();
				otam.isok = "notok";
				synchronized(sy) {
					try {
						otam.db_name_list = GetDBNameList(sd);
						otam.isok = "ok";
					} catch (Exception ee) {
					}
				}
				String s = gson.toJson(otam);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	
		
		post(new Route("/pushweb"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitPushNewWebPostMsg spnwpm = null;
				Gson gson = new Gson();
				try {
					spnwpm = gson.fromJson(request.body(), SubmitPushNewWebPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPushNewWebPostMsg opnwpm = new OutgoingPushNewWebPostMsg();
					opnwpm.isok = "notok";
					return gson.toJson(opnwpm);
				}
				OutgoingPushNewWebPostMsg opnwpm = new OutgoingPushNewWebPostMsg();
				opnwpm.isok = "notok";
				try {
					opnwpm.messages = PushNewWebToDBs(sd, spnwpm);
					opnwpm.isok = "ok";
				} catch (Exception ee) {
				}
				String s = gson.toJson(opnwpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	

		post(new Route("/savetag") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitSaveTagPostMsg sstpm = null;
				Gson gson = new Gson();
				try {
					sstpm = gson.fromJson(request.body(), SubmitSaveTagPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingSaveTagPostMsg ostpm = new OutgoingSaveTagPostMsg();
					ostpm.isok = "notok";
					return gson.toJson(ostpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey("_tags_")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("_tags_", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("_tags_");
				synchronized(sy) {
					try {
						resp = SaveTag(sd, sstpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingSaveTagPostMsg ostpm = new OutgoingSaveTagPostMsg();
				ostpm.isok = isok;
				if (isok.equals("ok")) {
					ostpm.errors = resp.get(0);
					ostpm.trtext = resp.get(1);
					ostpm.tagid = resp.get(2);
				}
				String s = gson.toJson(ostpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});		
		
		post(new Route("/deletetag") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitDeleteTagPostMsg sdtpm = null;
				Gson gson = new Gson();
				try {
					sdtpm = gson.fromJson(request.body(), SubmitDeleteTagPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey("_tags_")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("_tags_", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("_tags_");
				String res = "";
				synchronized(sy) {
					try {
						res = DeleteTag(sd, sdtpm);
						if (res.equals("")) isok = "ok";
						else isok = res;
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		
		
		post(new Route("/updatetagassign") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitTagAssignPostMsg stapm = null;
				Gson gson = new Gson();
				try {
					stapm = gson.fromJson(request.body(), SubmitTagAssignPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey("_tags_")) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put("_tags_", gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get("_tags_");
				synchronized(sy) {
					try {
						UpdateTagAssign(sd, stapm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		
		
		get(new Route("/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						String s = GetDBInfo2(gs,sd,-1, "Credible Sets", 0);
						if (dump_log) System.out.println("RESPONSE: "+s);
						return s;
					} catch (Exception ee) {
						ee.printStackTrace();
						response.status(400);
						return "Unable to load gene "+gs;
					}
				}
			}
		});

		post(new Route("/updategwascomment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateGWASCommentPostMsg ugcpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					ugcpm = gson.fromJson(request.body(), UpdateGWASCommentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						UpdateGWASComment(gs, sd, ugcpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/updategenephencomment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateGenePhenCommentPostMsg ugpcpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					ugpcpm = gson.fromJson(request.body(), UpdateGenePhenCommentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						UpdateGenePhenComment(gs, sd, ugpcpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/updatedetailcomment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateDetailCommentPostMsg udcpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					udcpm = gson.fromJson(request.body(), UpdateDetailCommentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						UpdateDetailComment(gs, sd, udcpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/createnewcredibleset/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				CreateNewCredibleSetPostMsg cncspm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					cncspm = gson.fromJson(request.body(), CreateNewCredibleSetPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						resp = CreateNewCredibleSet(gs, sd, cncspm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewCredibleSetPostMsg oncspm = new OutgoingNewCredibleSetPostMsg();
				oncspm.isok = isok;
				if (isok.equals("ok")) {
					oncspm.errors = resp.get(0);
					oncspm.csname = resp.get(1);
				}
				String s = gson.toJson(oncspm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});
		
		post(new Route("/deletecredibleset/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				DeleteCredibleSetPostMsg dcspm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					dcspm = gson.fromJson(request.body(), DeleteCredibleSetPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingOverlapPostMsg opm = new OutgoingOverlapPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						Vector<ArrayList<Integer>> v = DeleteCredibleSet(gs, sd, dcspm);
						ArrayList<Integer> eqtl_ids = v.elementAt(0);
						ArrayList<Integer> overlap_counts = v.elementAt(1);
						ArrayList<Integer> do_redo = v.elementAt(2);
						opm.eqtl_ids = gson.toJson(eqtl_ids, ArrayList.class);
						opm.overlap_counts = gson.toJson(overlap_counts, ArrayList.class);
						if (do_redo.get(0)==1) opm.sentcontents = GetDBInfo2(gs,sd,2,dcspm.svg_display_mode,dcspm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/updateeqtlcomment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateEQTLCommentPostMsg uecpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					uecpm = gson.fromJson(request.body(), UpdateEQTLCommentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						UpdateEQTLComment(gs, sd, uecpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/updatesourcecomment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateSourceCommentPostMsg uscpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					uscpm = gson.fromJson(request.body(), UpdateSourceCommentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {	
					try {
						UpdateSourceComment(gs, sd, uscpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/updatesourcereview/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateSourceReviewPostMsg usrpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					usrpm = gson.fromJson(request.body(), UpdateSourceReviewPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				Vector<String> ret = new Vector<>();
				synchronized(sy) {	
					try {
						ret = UpdateSourceReview(gs, sd, usrpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingSourceReviewPostMsg osrpm = new OutgoingSourceReviewPostMsg();
				osrpm.isok = isok;
				if (isok.equals("ok")) {
					osrpm.unreviewed_count = ret.get(0);
					osrpm.total_count = ret.get(1);
				}
				String s = gson.toJson(osrpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/submitnewgwasrow/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewGWASRowPostMsg sngrpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					sngrpm = gson.fromJson(request.body(), SubmitNewGWASRowPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewGWASRowPostMsg ongrpm = new OutgoingNewGWASRowPostMsg();
					ongrpm.isok = "notok";
					return gson.toJson(ongrpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewGWASRow(gs, sd, sngrpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewGWASRowPostMsg ongrpm = new OutgoingNewGWASRowPostMsg();
				ongrpm.isok = isok;
				if (isok.equals("ok")) {
					ongrpm.errors = resp.get(0);
					ongrpm.trtext = resp.get(1);
					ongrpm.gwasdbid = resp.get(2);
				}
				String s = gson.toJson(ongrpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/createneweqtl/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewEQTLRowPostMsg snerpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					snerpm = gson.fromJson(request.body(), SubmitNewEQTLRowPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewEQTLRowPostMsg onerpm = new OutgoingNewEQTLRowPostMsg();
					onerpm.isok = "notok";
					return gson.toJson(onerpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewEQTLRow(gs, sd, snerpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewEQTLRowPostMsg onerpm = new OutgoingNewEQTLRowPostMsg();
				onerpm.isok = isok;
				if (isok.equals("ok")) {
					onerpm.errors = resp.get(0);
					onerpm.newrow = resp.get(1);
					onerpm.eqtldbid = resp.get(2);
				}
				String s = gson.toJson(onerpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});

		post(new Route("/submitnewdetail/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewDetailPostMsg sndpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					sndpm = gson.fromJson(request.body(), SubmitNewDetailPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewDetailPostMsg ondpm = new OutgoingNewDetailPostMsg();
					ondpm.isok = "notok";
					return gson.toJson(ondpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewDetail(gs, sd, sndpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewDetailPostMsg ondpm = new OutgoingNewDetailPostMsg();
				ondpm.isok = isok;
				if (isok.equals("ok")) {
					ondpm.errors = resp.get(0);
					ondpm.trtext = resp.get(1);
					ondpm.detaildbid = resp.get(2);
				}
				String s = gson.toJson(ondpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});		

		post(new Route("/downloadfile/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				DownloadFilePostMsg dfpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					dfpm = gson.fromJson(request.body(), DownloadFilePostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingDownloadFilePostMsg odfpm = new OutgoingDownloadFilePostMsg();
					odfpm.isok = "notok";
					return gson.toJson(odfpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = DownloadFile(gs, sd, dfpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingDownloadFilePostMsg odfpm = new OutgoingDownloadFilePostMsg();
				odfpm.isok = isok;
				if (isok.equals("ok")) {
					odfpm.name = resp.get(0);
					odfpm.b64 = resp.get(1);
				}
				String s = gson.toJson(odfpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});				

		post(new Route("/oktoremoveref/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				OkToRemoveRefPostMsg otrrpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					otrrpm = gson.fromJson(request.body(), OkToRemoveRefPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingOkToRemoveRefPostMsg ootrrpm = new OutgoingOkToRemoveRefPostMsg();
					ootrrpm.isok = "notok";
					return gson.toJson(ootrrpm);
				}
				String isok = "notok";
				Vector<String> ret = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						ret = OkToRemoveRef(gs, sd, otrrpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingOkToRemoveRefPostMsg ootrrpm = new OutgoingOkToRemoveRefPostMsg();
				ootrrpm.isok = isok;
				if (isok.equals("ok")) {
					ootrrpm.problems = ret.get(0);
					ootrrpm.unreviewed_count = ret.get(1);
					ootrrpm.total_count = ret.get(2);
				}
				String s = gson.toJson(ootrrpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});				

		post(new Route("/submitnewpubmed/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewPubmedPostMsg snppm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					snppm = gson.fromJson(request.body(), SubmitNewPubmedPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewPubmedPostMsg onppm = new OutgoingNewPubmedPostMsg();
					onppm.isok = "notok";
					return gson.toJson(onppm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewPubmed(gs, sd, snppm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewPubmedPostMsg onppm = new OutgoingNewPubmedPostMsg();
				onppm.isok = isok;
				if (isok.equals("ok")) {
					onppm.errors = resp.get(0);
					onppm.trtext = resp.get(1);
					onppm.sourcedocumentsid = resp.get(2);
					onppm.trtext2 = resp.get(3);
					onppm.unreviewed_count = resp.get(4);
					onppm.total_count = resp.get(5);
				}
				String s = gson.toJson(onppm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});		

		post(new Route("/submitnewbiorxiv/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewBiorxivPostMsg snbpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					snbpm = gson.fromJson(request.body(), SubmitNewBiorxivPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewBiorxivPostMsg onbpm = new OutgoingNewBiorxivPostMsg();
					onbpm.isok = "notok";
					return gson.toJson(onbpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewBiorxiv(gs, sd, snbpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewBiorxivPostMsg onbpm = new OutgoingNewBiorxivPostMsg();
				onbpm.isok = isok;
				if (isok.equals("ok")) {
					onbpm.errors = resp.get(0);
					onbpm.trtext = resp.get(1);
					onbpm.sourcedocumentsid = resp.get(2);
					onbpm.trtext2 = resp.get(3);
					onbpm.unreviewed_count = resp.get(4);
					onbpm.total_count = resp.get(5);
				}
				String s = gson.toJson(onbpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});		

		post(new Route("/submitnewweb/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewWebPostMsg snwpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					snwpm = gson.fromJson(request.body(), SubmitNewWebPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewWebPostMsg onwpm = new OutgoingNewWebPostMsg();
					onwpm.isok = "notok";
					return gson.toJson(onwpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewWeb(gs, sd, snwpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewWebPostMsg onwpm = new OutgoingNewWebPostMsg();
				onwpm.isok = isok;
				if (isok.equals("ok")) {
					onwpm.errors = resp.get(0);
					onwpm.trtext = resp.get(1);
					onwpm.sourcedocumentsid = resp.get(2);
					onwpm.trtext2 = resp.get(3);
					onwpm.unreviewed_count = resp.get(4);
					onwpm.total_count = resp.get(5);
				}
				String s = gson.toJson(onwpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		
		
		post(new Route("/submitnewfile/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitNewFilePostMsg snfpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					snfpm = gson.fromJson(request.body(), SubmitNewFilePostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingNewFilePostMsg onwpm = new OutgoingNewFilePostMsg();
					onwpm.isok = "notok";
					return gson.toJson(onwpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitNewFile(gs, sd, snfpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingNewFilePostMsg onfpm = new OutgoingNewFilePostMsg();
				onfpm.isok = isok;
				if (isok.equals("ok")) {
					onfpm.errors = resp.get(0);
					onfpm.trtext = resp.get(1);
					onfpm.sourcedocumentsid = resp.get(2);
					onfpm.trtext2 = resp.get(3);
					onfpm.unreviewed_count = resp.get(4);
					onfpm.total_count = resp.get(5);
				}
				String s = gson.toJson(onfpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	
		
		post(new Route("/submitdetailsectionassignment/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				SubmitSectionAssignmentPostMsg ssapm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					ssapm = gson.fromJson(request.body(), SubmitSectionAssignmentPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingSectionAssignmentPostMsg osapm = new OutgoingSectionAssignmentPostMsg();
					osapm.isok = "notok";
					return gson.toJson(osapm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = SubmitDetailSectionAssignment(gs, sd, ssapm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingSectionAssignmentPostMsg osapm = new OutgoingSectionAssignmentPostMsg();
				osapm.isok = isok;
				if (isok.equals("ok")) {
					osapm.old_section_lc = resp.get(0).toLowerCase();
					osapm.old_section_source_documents_id = resp.get(1);
					osapm.old_section_revised_rows = resp.get(2);
					osapm.new_section_lc = resp.get(3).toLowerCase();
					osapm.new_section_source_documents_id = resp.get(4);
					osapm.new_section_revised_rows = resp.get(5);
				}
				String s = gson.toJson(osapm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		

		post(new Route("/removedetail/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				RemoveDetailPostMsg rdpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					rdpm = gson.fromJson(request.body(), RemoveDetailPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingRemoveDetailPostMsg ordpm = new OutgoingRemoveDetailPostMsg();
					ordpm.isok = "notok";
					return gson.toJson(ordpm);
				}
				String isok = "notok";
				Vector<String> resp = new Vector<>();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						resp = RemoveDetail(gs, sd, rdpm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingRemoveDetailPostMsg ordpm = new OutgoingRemoveDetailPostMsg();
				ordpm.isok = isok;
				if (isok.equals("ok")) {
					ordpm.old_section_lc = resp.get(0).toLowerCase();
					ordpm.old_section_source_documents_id = resp.get(1);
					ordpm.old_section_revised_rows = resp.get(2);
					ordpm.source_documents_id = resp.get(3);
				}
				String s = gson.toJson(ordpm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		

		post(new Route("/removegwasrow/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				RemoveGWASRowPostMsg rgrpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					rgrpm = gson.fromJson(request.body(), RemoveGWASRowPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				OutgoingOverlapPostMsg opm = new OutgoingOverlapPostMsg();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						Vector<ArrayList<Integer>> v = RemoveGWASRow(gs, sd, rgrpm);
						ArrayList<Integer> eqtl_ids = v.elementAt(0);
						ArrayList<Integer> overlap_counts = v.elementAt(1);
						ArrayList<Integer> do_redo = v.elementAt(2);
						opm.eqtl_ids = gson.toJson(eqtl_ids, ArrayList.class);
						opm.overlap_counts = gson.toJson(overlap_counts, ArrayList.class);
						if (do_redo.get(0)==1) opm.sentcontents = GetDBInfo2(gs,sd,2,rgrpm.svg_display_mode,rgrpm.hidenoncoding);
						isok = "ok";
					} catch (Exception ee) {ee.printStackTrace(); isok = "notok";}
				}
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});
		
		post(new Route("/removeeqtlrow/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				RemoveEQTLRowPostMsg rerpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					rerpm = gson.fromJson(request.body(), RemoveEQTLRowPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				boolean remake_svg = true;
				OutgoingPostMsg opm = new OutgoingPostMsg();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						remake_svg = RemoveEQTLRow(gs, sd, rerpm);
						if (remake_svg) opm.sentcontents = GetDBInfo2(gs,sd,2,rerpm.svg_display_mode,rerpm.hidenoncoding);
						isok = "ok";
					} catch (Exception ee) {ee.printStackTrace(); isok = "notok";}
				}
				opm.isok = isok;
				String s = gson.toJson(opm);	
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});

		post(new Route("/updategwassvgdisplayname/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateGWASSVGDisplayNamePostMsg usdnpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					usdnpm = gson.fromJson(request.body(), UpdateGWASSVGDisplayNamePostMsg.class);
					//usdnpm.svgdisplayname = EncodeLikeJavascript.decodeURIComponent(usdnpm.svgdisplayname);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				boolean remake_svg = true;
				OutgoingPostMsg opm = new OutgoingPostMsg();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						remake_svg = UpdateGWASSVGDisplayName(gs, sd, usdnpm);
						if (remake_svg) opm.sentcontents = GetDBInfo2(gs,sd,2,usdnpm.svg_display_mode,usdnpm.hidenoncoding);
						isok = "ok";
					} catch (Exception ee) {ee.printStackTrace(); isok = "notok";}
				}
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});		

		post(new Route("/updateeqtlsvgdisplayname/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateEQTLSVGDisplayNamePostMsg usdnpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					usdnpm = gson.fromJson(request.body(), UpdateEQTLSVGDisplayNamePostMsg.class);
					//usdnpm.svgdisplayname = EncodeLikeJavascript.decodeURIComponent(usdnpm.svgdisplayname);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				boolean remake_svg = true;
				OutgoingPostMsg opm = new OutgoingPostMsg();
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						remake_svg = UpdateEQTLSVGDisplayName(gs, sd, usdnpm);
						if (remake_svg) opm.sentcontents = GetDBInfo2(gs,sd,2,usdnpm.svg_display_mode,usdnpm.hidenoncoding);
						isok = "ok";
					} catch (Exception ee) {ee.printStackTrace(); isok = "notok";}
				}
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		

		post(new Route("/updatesummary/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateSummaryPostMsg uspm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					uspm = gson.fromJson(request.body(), UpdateSummaryPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				String isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						UpdateSummary(gs, sd, uspm);
						isok = "ok";
					} catch (Exception ee) {}
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = isok;
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});

		post(new Route("/updategwasshowhide/:symbol") {
			@Override
			public Object handle(Request request, Response response) {	
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateGWASShowHidePostMsg uspm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					uspm = gson.fromJson(request.body(), UpdateGWASShowHidePostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingOverlapPostMsg opm = new OutgoingOverlapPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						Vector<ArrayList<Integer>> v = UpdateGWASShowHide(gs, sd, uspm);
						ArrayList<Integer> eqtl_ids = v.elementAt(0);
						ArrayList<Integer> overlap_counts = v.elementAt(1);
						ArrayList<Integer> do_redo = v.elementAt(2);
						opm.eqtl_ids = gson.toJson(eqtl_ids, ArrayList.class);
						opm.overlap_counts = gson.toJson(overlap_counts, ArrayList.class);
						if (do_redo.get(0)==1) opm.sentcontents = GetDBInfo2(gs,sd,2,uspm.svg_display_mode,uspm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});
		
		post(new Route("/updateshowhidenoncoding/:symbol") {
			@Override
			public Object handle(Request request, Response response) {	
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateShowHideNonCodingPostMsg ushncpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					ushncpm = gson.fromJson(request.body(), UpdateShowHideNonCodingPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						opm.sentcontents = GetDBInfo2(gs,sd,2,ushncpm.svg_display_mode,ushncpm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});

		post(new Route("/updatemarkerforld/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateMarkerForLDPostMsg umflpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					umflpm = gson.fromJson(request.body(), UpdateMarkerForLDPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						UpdateMarkerForLD(gs, sd, umflpm);
						opm.sentcontents = GetDBInfo2(gs,sd,2,umflpm.svg_display_mode,umflpm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});

		post(new Route("/updateeqtlshowhide/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateEQTLShowHidePostMsg ueshpm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();
				try {
					ueshpm = gson.fromJson(request.body(), UpdateEQTLShowHidePostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingPostMsg opm = new OutgoingPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						UpdateEQTLShowHide(gs, sd, ueshpm);
						opm.sentcontents = GetDBInfo2(gs,sd,2,ueshpm.svg_display_mode,ueshpm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});

		post(new Route("/updategwasmarkerequivalenceset/:symbol") {
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				UpdateGWASMarkerEquivalenceSetPostMsg umespm = null;
				String gs = request.params(":symbol");
				gs = EncodeLikeJavascript.decodeURIComponent(gs);
				Gson gson = new Gson();//GsonBuilder().create();
				try {
					umespm = gson.fromJson(request.body(), UpdateGWASMarkerEquivalenceSetPostMsg.class);
				} catch (JsonSyntaxException e) {
					OutgoingPostMsg opm = new OutgoingPostMsg();
					opm.isok = "notok";
					return gson.toJson(opm);
				}
				OutgoingOverlapPostMsg opm = new OutgoingOverlapPostMsg();
				opm.isok = "notok";
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				synchronized(sy) {
					try {
						Vector<ArrayList<Integer>> v = UpdateGWASMarkerEquivalenceSet(gs, sd, umespm);
						ArrayList<Integer> eqtl_ids = v.elementAt(0);
						ArrayList<Integer> overlap_counts = v.elementAt(1);
						ArrayList<Integer> do_redo = v.elementAt(2);
						opm.eqtl_ids = gson.toJson(eqtl_ids, ArrayList.class);
						opm.overlap_counts = gson.toJson(overlap_counts, ArrayList.class);
						if (do_redo.get(0)==1) opm.sentcontents = GetDBInfo2(gs,sd,2,umespm.svg_display_mode,umespm.hidenoncoding);
						opm.isok = "ok";
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			} 
		});

		get(new Route("/get_pub_list/:symbol"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				String gs = request.params(":symbol");
				Gson gson = new Gson();
				gs= EncodeLikeJavascript.decodeURIComponent(gs);
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				OutgoingPubListMsg opm = new OutgoingPubListMsg();
				opm.isok = "notok";
				synchronized(sy) {
					try {
						opm.pub_list = GetPubList(gs, sd);
						opm.isok = "ok";
					} catch (Exception ee) {
					}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});	
		
		get(new Route("/get_pub_and_gene_list/:symbol"){
			@Override
			public Object handle(Request request, Response response) {
				if (dump_log) System.out.println("REQUEST: "+request.body());
				String gs = request.params(":symbol");
				Gson gson = new Gson();
				gs= EncodeLikeJavascript.decodeURIComponent(gs);
				if (!gene_synchronize_objects.containsKey(gs)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gs, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gs);
				OutgoingPubAndGeneListMsg opm = new OutgoingPubAndGeneListMsg();
				opm.isok = "notok";
				synchronized(sy) {
					try {
						Vector<String> ret = GetPubAndGeneList(gs, sd);
						opm.pub_list = ret.get(0);
						opm.gene_list = ret.get(1);
						opm.isok = "ok";
					} catch (Exception ee) {
					}
				}
				String s = gson.toJson(opm);
				if (dump_log) System.out.println("RESPONSE: "+s);
				return s;
			}
		});		
	}
	
	public static String GetHTMLStart(String tab_name, String backend_version, String db_version) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<!doctype html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"//fonts.googleapis.com/css?family=Lato\" />\n");		
		sb.append("<title>"+tab_name+"</title>\n");
		sb.append("<style>\n");
		sb.append("  #paper1{padding: 25px;}\n");
		sb.append("  #paper1{padding-bottom: 0px;}\n");
		sb.append("  #paper1{ background-color: #eeeeee; }\n");
		sb.append("  #mouseoverdiv{ background-color: #eeeeee; }\n");				
		sb.append("  textarea { resize: none; outline: none; padding: 0px 0px 0px 0px !important; margin: 0px 0px 0px 0px !important; border: 0px solid #DDDDDD !important; }\n");
		sb.append("  textarea:focus { resize: none; padding: 0px 0px 0px 0px; margin: 0px 0px 0px 0px; border: 0px solid rgba(81, 203, 238, 1); }\n");	
		sb.append("</style>\n");
		sb.append("<script type=\"text/javascript\" src=\"content/tgn.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"content/tgn_tags.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"jquery/jquery-2.2.4.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"FileSaver.js-master/FileSaver.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"lightbox2-master/dist/js/lightbox.js\"></script>\n");
		sb.append("<link rel=\"stylesheet\" href=\"lightbox2-master/dist/css/lightbox.css\" type=\"text/css\"/>\n");	 		
		sb.append("<script type=\"text/javascript\" src=\"tablesorter-master/js/jquery.tablesorter.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"tablesorter-master/addons/pager/jquery.tablesorter.pager.js\"></script>\n");
		sb.append("<link rel=\"stylesheet\" href=\"tablesorter-master/addons/pager/jquery.tablesorter.pager.css\" type=\"text/css\"/>\n");	    
		sb.append("<script type=\"text/javascript\" src=\"select2-4.0.3/dist/js/select2.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"tablesorter-master/js/jquery.tablesorter.widgets.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"huebee/huebee.pkgd.js\"></script>\n");
		sb.append("<link rel=\"stylesheet\" href=\"select2-4.0.3/dist/css/select2.css\" type=\"text/css\"/>\n");
		sb.append("<link rel=\"stylesheet\" href=\"tablesorter-master/css/theme.blue.css\" type=\"text/css\"/>\n");
		sb.append("<script type=\"text/javascript\" src=\"jquery-ui-1.12.1.custom/jquery-ui.js\"></script>\n");
		sb.append("<link rel=\"stylesheet\" href=\"jquery-ui-1.12.1.custom/jquery-ui.css\" type=\"text/css\"/>\n");
		sb.append("<script type=\"text/javascript\" src=\"tablesorter-master/js/parsers/parser-input-select.js\"></script>\n");	   	    
		sb.append("<script type=\"text/javascript\" src=\"tablesorter-master/js/widgets/widget-output.js\"></script>\n");	    
		sb.append("<script type=\"text/javascript\" src=\"bootstrap-3.3.7-dist/js/bootstrap.js\"></script>\n");
		sb.append("<link rel=\"stylesheet\" href=\"bootstrap-3.3.7-dist/css/bootstrap.css\" type=\"text/css\"/>\n");
		sb.append("<link rel=\"stylesheet\" href=\"huebee/huebee.css\" type=\"text/css\"/>\n");

		sb.append("<style>\n");
		sb.append("  .select2-selection--single { height: 100% !important; line-height: 18px !important; padding-left: 4px !important; padding-right: 8px !important}\n");
		sb.append("  .select2-selection__rendered{ word-wrap: break-word !important; text-overflow: inherit !important; white-space: normal !important; line-height: 18px !important; padding-left: 4px !important; padding-right: 8px !important}\n");
		sb.append("  .select2-results__option { padding: 0px 0px 0px 0px; }\n");
		sb.append("  .ak_table_button { padding: 1px; border: 1px;}\n");
		sb.append("  .ak_table_button:disabled { background: #c60071 !important;}\n");
		sb.append("  td { padding: 1px !important; background: transparent !important; word-wrap: break-word; white-space: normal; word-break: normal;}\n");
		//sb.append("  td { padding: 1px !important; background: transparent !important; word-wrap: break-word; white-space: normal; word-break:break-all;}\n");
		sb.append("  .akwrapper { overlow-x:auto; overflow-y:hidden; width:1300px;}\n");
		sb.append("  .akwrapper table { width:auto; table-layout:fixed;}\n");
		sb.append("  .ui-accordion-header {color:#ffffff !important; background-color:#00739c !important; border-bottom-color:#00739c !important; border-left-color:#00739c !important; border-right-color:#00739c !important; border-top-color:#00739c !important; padding-top:2px !important; padding-bottom:2px !important;}\n");
		sb.append("  #aktmptable td {text-align: center;}\n");
		sb.append("  .contain_img {object-fit: contain;}\n");
		sb.append("  * { font-family: Lato, Sans-serif; }\n");
		//sb.append("  :not(.aktest) { font-family: Lato, Sans-serif; }\n");
		sb.append("  .ak_tablecell { font: 12px/18px Lato, Sans-serif !important; }\n");
		sb.append("  .ak_table_button { font: 12px/18px Lato, Sans-serif !important; }\n");
		sb.append("  select { font: 12px/18px Lato, Sans-serif !important; }\n");
		sb.append("  label { font: 14px/20px Lato, Sans-serif !important; }\n");
		sb.append("  .pager { font: 12px/18px Lato, Sans-serif !important; }\n");
		sb.append("  textarea { font-family: Lato, Sans-serif !important; }\n");
		sb.append("  input { font: 12px/18px Lato, Sans-serif !important; }\n");	
		sb.append("  .select2 { font: 12px/18px Lato, Sans-serif !important; }\n");
		sb.append("  .select2-results__option { font: 12px/18px Lato, Sans-serif !important; }\n");
		//sb.append("  .tgn_agree { display:none; width:100%; height:100%; position:fixed; left:0; top:0; z-index:101; background: rgba(0,0,0,0.5); }\n");
		//sb.append("  .agree_ta { margin-left:15%; margin-right:15%; margin-top:15%; margin-bottom:15%; display:inline-block; }\n");
		sb.append("  body,html{ background-color: #eeeeee; }\n");
		sb.append("  .akmodal {display: none; position: fixed; z-index: 1000; padding-top: 100px; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgb(0,0,0); background-color: rgba(0,0,0,0.85);}\n");
		sb.append("</style>\n");		
		sb.append("</head>\n");
		
		sb.append("<body>\n");
		
		// nav bar
		
		sb.append("<nav class=\"navbar navbar-default\" style=\"background:#222; margin-bottom:0px !important;\">\n");
		sb.append("  <div class=\"container-fluid\">\n");
		sb.append("    <div class=\"navbar-header\">\n");
		sb.append("      <a href=\"http://www.eisai.com\" class=\"navbar-left\" target=\"_blank\"><img src=\"./logo/Eisai Logo.png\" height=\"39px\" style=\"margin-top:6px; margin-bottom:6px;\" align=\"middle\"></a>"); // height was 50 w/o any border
		if (backend_version.equals("")) {
			sb.append("      <p class=\"navbar-text\" style=\"font-size:15px; color:white; font-weight:bold;\">Target Gene Notebook</p>\n");	
		} else { 
			sb.append("      <p class=\"navbar-text\" style=\"font-size:15px; color:white; font-weight:bold;\">Target Gene Notebook "+backend_version+":"+db_version+"</p>\n");
		}
		sb.append("    </div>\n");
		/*
		sb.append("    <ul class=\"nav navbar-nav navbar-right\">\n");
		sb.append("      <li><a style=\"font-size:15px; color:white; font-weight:bold;\" href=\"./about.html\" target=\"_tgn_other\">About</a></li>\n");
		sb.append("      <li><a style=\"font-size:15px; color:white; font-weight:bold;\" href=\"./terms.html\" target=\"_tgn_other\">Terms</a></li>\n");
		sb.append("      <li><a style=\"font-size:15px; color:white; font-weight:bold;\" href=\"./contact.html\" target=\"_tgn_other\">Contact</a></li>\n");
		sb.append("      <li><a style=\"font-size:15px; color:white; font-weight:bold;\" href=\"./faq.html\" target=\"_tgn_other\">FAQ</a></li>\n");
		sb.append("      <li><a style=\"font-size:15px; color:white; font-weight:bold;\" href=\"content/TGN_UserGuide.pdf\" target=\"_tgn_userguide\">User Guide</a></li>\n");
		sb.append("    </ul>\n");
		*/
		sb.append("  </div>\n");
		sb.append("</nav>\n");

		// license
		/*
		sb.append("<div class=\"tgn_agree\" style=\"text-align:center;\">\n");
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("public/content/agreement.txt");
		String agree_text = IOUtils.toString(is);
		is.close();		
		sb.append(" <section style=\"margin-top:0px !important; display:inline-block; width:70%; height:85%;\">\n");
		sb.append(" <textarea readonly style=\"display:block; width:100%; height:90%;\">"+StringEscapeUtils.escapeHtml4(agree_text)+"</textarea>\n");
		sb.append(" <button type=\"button\" style=\"display:block; margin:auto;\" onclick=\"AcceptAgreement()\" class=\"ak_table_button\">Agree</button>\n");
		sb.append("  </section>");				
		sb.append("</div>\n");
		*/
		return sb.toString();
	}
	
	public static String UnreviewedCheckin(String storageDir) throws Exception {
		ArrayList<String> gene_list = new ArrayList<>();
		Gson gson = new Gson();
		Path p = Paths.get(storageDir);
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
				for (Path entry: stream) {
					String just_gene = entry.getFileName().toString();
					just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
					String just_gene_uc = just_gene.toUpperCase();
					if (just_gene_uc.equals("_TAGS")) continue;
					String dbFilePath = storageDir+"/"+just_gene+".sqlite";
					connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);  
					statement = connection.createStatement();
					int unreviewed_reference_count = 0;
					int total_reference_count = 0;
					rs = statement.executeQuery("select has_been_reviewed from source_documents");
					while (rs.next()) {
						int hbr = rs.getInt("has_been_reviewed");
						total_reference_count++;
						if (hbr==0) unreviewed_reference_count++;		
					}
					rs.close();
					statement.close();
					connection.close();
					LinkedHashMap<String, String> map = new LinkedHashMap<>();
					map.put("gene", just_gene_uc); 
					map.put("unreviewed", unreviewed_reference_count+" out of "+total_reference_count+" not reviewed");
					String jsonObs = gson.toJson(map, LinkedHashMap.class);
					gene_list.add(jsonObs);
				}
			} catch (DirectoryIteratorException e) {
				throw e.getCause();
			}
			return gene_list.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static Vector<String> SaveTag(String storage_dir, SubmitSaveTagPostMsg sstpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		try {
			String errors = "";
			connection = GetTagDBConnection("jdbc:sqlite:"+storage_dir+"_tags.sqlite");
			statement = connection.createStatement();	

			sstpm.short_name = sstpm.short_name.trim();
			sstpm.long_name = sstpm.long_name.trim();
			sstpm.description = sstpm.description.trim();
			sstpm.color = sstpm.color.replaceAll(" ", "");
			
			if (sstpm.short_name.equals("")) errors = "No short name was provided.";
			if (sstpm.long_name.equals("")) {
				if (!errors.equals("")) errors += "\n";
				errors += "No long name was provided.";
			}
			if (sstpm.tag_class_id.equals("-1")) {
				if (!errors.equals("")) errors += "\n";
				errors += "No tag class was provided.";
			}
			
			rs = statement.executeQuery("select short_name, long_name from tags where id != "+sstpm.tag_id);
			while(rs.next()) {
				String sn = rs.getString("short_name");
				String ln = rs.getString("long_name");
				if (sn.equalsIgnoreCase(sstpm.short_name)) {
					if (!errors.equals("")) errors += "\n";
					errors += "Short name is not unique.";	
				}
				if (ln.equalsIgnoreCase(sstpm.long_name)) {
					if (!errors.equals("")) errors += "\n";
					errors += "Long name is not unique.";	
				}
			}
			rs.close();
		
			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");
				return toret;
			}

			if (sstpm.tag_id.equals("-1")) {

				String isql = "insert into tags (" +
						"short_name,"+
						"long_name,"+
						"description,"+
						"color,"+
						"tag_class_id"+
						" ) values (?,?,?,?,?)";

				pstatement = connection.prepareStatement(isql);
				pstatement.setString(1, sstpm.short_name);
				pstatement.setString(2, sstpm.long_name);
				pstatement.setString(3, sstpm.description);
				pstatement.setString(4, sstpm.color);
				pstatement.setString(5, sstpm.tag_class_id);
				pstatement.executeUpdate();
				rs = pstatement.getGeneratedKeys();
				while(rs.next()) {
					sstpm.tag_id = rs.getString(1);
				}
				rs.close();
			} else {
				String usql = "update tags " +
						"set short_name = ?,"+
						"long_name = ?,"+
						"description = ?,"+
						"color = ?,"+
						"tag_class_id = ?"+
						" where id = ?";

				pstatement = connection.prepareStatement(usql);
				pstatement.setString(1, sstpm.short_name);
				pstatement.setString(2, sstpm.long_name);
				pstatement.setString(3, sstpm.description);
				pstatement.setString(4, sstpm.color);
				pstatement.setString(5, sstpm.tag_class_id);
				pstatement.setString(6, sstpm.tag_id);
				pstatement.executeUpdate();
				
			}
			
			//InputStream isf = Thread.currentThread().getContextClassLoader().getResourceAsStream("public/content/latoboldwoff.b64");
			//String lato_b64 = IOUtils.toString(isf);
			//isf.close();	
			StringBuffer sb = new StringBuffer();
			rs = statement.executeQuery("select t.id as id, short_name, long_name, description, color, c.id as class_id, c.name as class_name from tag_classes c, tags t where t.tag_class_id=c.id and t.id = "+sstpm.tag_id);
			while(rs.next()){
				String tid = rs.getString("id");
				String short_name = rs.getString("short_name");
				String long_name = rs.getString("long_name");
				String description = rs.getString("description");
				String color = rs.getString("color");
				String class_id = rs.getString("class_id");
				String class_name = rs.getString("class_name");
				color = color.replaceAll(" ", "");
				int red = Integer.parseInt(color.substring(color.indexOf("(")+1, color.indexOf(",")));
				int green = Integer.parseInt(color.substring(color.indexOf(",")+1, color.lastIndexOf(",")));
				int blue = Integer.parseInt(color.substring(color.lastIndexOf(",")+1, color.indexOf(")")));
				
				String tagsvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100px\" height=\"25px\" viewBox=\"0 0 100 25\" preserveAspectRatio=\"xMidYMid meet\" onload='tag_init(evt)'>\n";
				tagsvg+="<script type=\"application/ecmascript\"> <![CDATA[\n";
				tagsvg+="  function tag_init(evt) {\n";
				tagsvg+="    var svgDocument = evt.target;\n";
				tagsvg+="    var tt = svgDocument.getElementsByTagName('text')[0];\n";
				tagsvg+="    while (tt.getBBox().width>75) {\n";
				tagsvg+="      var sz = tt.getAttribute('font-size');\n";
				tagsvg+="      if (sz<=1) {break;}\n";
				tagsvg+="      sz--;\n";
				tagsvg+="      tt.setAttribute('font-size', sz);\n";
				tagsvg+="    }\n";
				tagsvg+="  }\n";
				tagsvg+="]]> </script>\n";
				
				if (class_name.equals("Point person")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 A 12.5 12.5 0 0 0 100 25 L 0 25 A 12.5 12.5 0 0 0 0 0 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Disease association")) {
					tagsvg+="<path  d=\"M 0 12 L 10 0 L 90 0 L 100 12 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Pathway")) {
					tagsvg+="<path  d=\"M 0 0 L 90 0 L 90 8 L 100 8 L 100 17 L 90 17 L 90 25 L 0 25 L 0 17 L 10 17 L 10 8 L 0 8 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Publication")) {
					tagsvg+="<path  d=\"M 10 0 L 90 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else  if (class_name.equals("Eisai site")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Project name")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Collaboration")) {
					tagsvg+="<path  d=\"M 10 0 L 100 0 L 90 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Statistic")) {
					tagsvg+="<path  d=\"M 12.5 0 L 87.5 0 A 12.5 12.5 0 0 1 87.5 25 L 12.5 25 A 12.5 12.5 0 0 1 12.5 0 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				}
				
				String tcolor = "white";
				if (red+green+blue>500) tcolor="black";
				tagsvg+="<text style=\"text-anchor:middle; dominant-baseline:central; alignment-baseline:central;\" font-family=\"Lato, Sans-serif\" font-weight=\"800\" font-size=\"16\" x=\"50px\" y=\"12px\" fill=\""+tcolor+"\" >"+short_name+"</text>\n";
				tagsvg+="</svg>\n";

				String rowclass = "aktagrowclass"+tid;
				sb.append("  <tr id=\"tagid"+tid+"\" class=\"a_tag_row\" data-tag_color=\""+color+"\" data-tag_text_color=\""+tcolor+"\">\n");
				sb.append("  <td style=\"text-align: center; vertical-align:middle;\">"+tagsvg+"</td>");
				sb.append("    <td style=\"text-align: center; vertical-align:middle;\" data-class_id=\""+class_id+"\" class=\"ak_tablecell class_name\">"+StringEscapeUtils.escapeHtml4(class_name)+"</td>\n");
				sb.append("    <td style=\"text-align: center; vertical-align:middle;\" class=\"ak_tablecell short_name\">"+StringEscapeUtils.escapeHtml4(short_name)+"</td>\n");
				sb.append("    <td><textarea readonly class=\"ak_tablecell long_name\" style=\"background-color:#f5f5f5; width:100%; height:100%\" >"+StringEscapeUtils.escapeHtml4(long_name)+"</textarea></td>\n");
				sb.append("    <td><textarea readonly class=\"ak_tablecell description\" style=\"background-color:#f5f5f5; width:100%; height:100%\" >"+StringEscapeUtils.escapeHtml4(description)+"</textarea></td>\n");
				sb.append("<td style=\"vertical-align: middle;\"><button style=\"width: 40px; display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"AddEditTagOverlay("+tid+")\">Edit</button><button style=\"width: 40px; display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"DeleteTag("+tid+")\">Delete</button></td>\n");
				sb.append("  </tr>\n");
			}
			rs.close();
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(sstpm.tag_id);
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	
	
	public static String DeleteTag(String storage_dir, SubmitDeleteTagPostMsg sdtpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			Path p = Paths.get(storage_dir);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
				for (Path entry: stream) {
					String just_gene = entry.getFileName().toString();
					just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
					String just_gene_uc = just_gene.toUpperCase();
					if (just_gene_uc.equals("_TAGS")) continue;
					boolean has_tag = true;
					String dbFilePath = storage_dir+"/"+just_gene+".sqlite";
					connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);  
					statement = connection.createStatement();
					result = statement.executeQuery("select count(*) from applied_tags where tag_id = "+sdtpm.tag_id);
					int count = 1;
					while(result.next()){
						count = result.getInt(1);
					}
					if (count==0) has_tag = false;
					result.close();
					statement.close();
					connection.close();
					if (has_tag) {
						return "Remove all tag assignments before deleting tag.";
					}
				}
			} catch (DirectoryIteratorException e) {
				throw e.getCause();
			}
			connection = GetTagDBConnection("jdbc:sqlite:"+storage_dir+"_tags.sqlite");
			String dsql = "delete from tags where id = ?";
			pstatement = connection.prepareStatement(dsql);
			pstatement.setString(1, sdtpm.tag_id);
			pstatement.executeUpdate();
			return "";
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static Connection GetTagDBConnection(String dburl) throws Exception {
		Connection connection = null;
		ResultSet result = null;
		Statement statement = null;
		boolean all_ok = false;
		try {
			connection = DriverManager.getConnection(dburl);
			DatabaseMetaData md = connection.getMetaData();
			boolean tables_found = false;
			result = md.getTables(null, null, "tag_classes", null);
			while (result.next()) {
				tables_found = true;
			}
			result.close();
			if (!tables_found) {
				statement = connection.createStatement();
				statement.executeUpdate("CREATE TABLE tag_classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)");
				statement.executeUpdate("CREATE TABLE tags (id INTEGER PRIMARY KEY AUTOINCREMENT, short_name TEXT NOT NULL UNIQUE, long_name TEXT NOT NULL UNIQUE, description TEXT NOT NULL, color TEXT NOT NULL, tag_class_id INTEGER NOT NULL REFERENCES tag_classes(id))");
				statement.executeUpdate("insert into tag_classes (name) values ('Disease association')");
				statement.executeUpdate("insert into tag_classes (name) values ('Point person')");
				statement.executeUpdate("insert into tag_classes (name) values ('Pathway')");
				statement.executeUpdate("insert into tag_classes (name) values ('Publication')");
				statement.executeUpdate("insert into tag_classes (name) values ('Collaboration')");
				statement.executeUpdate("insert into tag_classes (name) values ('Project name')");
				statement.executeUpdate("insert into tag_classes (name) values ('Eisai site')");
				statement.executeUpdate("insert into tag_classes (name) values ('Statistic')");
			}
			all_ok = true;
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (!all_ok && connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static String GetTagSVG(String tag_id, Statement statement) throws Exception {
		ResultSet result = null;
		try {
			result = statement.executeQuery("select short_name, color, c.name as class_name, description from tag_classes c, tags t where t.tag_class_id=c.id and t.id = "+tag_id+" order by short_name");
			String tagsvg="";
			while(result.next()){
				String short_name = result.getString("short_name");
				String color = result.getString("color");
				String class_name = result.getString("class_name");
				String description = result.getString("description");
				if (result.wasNull()) description = "";
				color = color.replaceAll(" ", "");
				int red = Integer.parseInt(color.substring(color.indexOf("(")+1, color.indexOf(",")));
				int green = Integer.parseInt(color.substring(color.indexOf(",")+1, color.lastIndexOf(",")));
				int blue = Integer.parseInt(color.substring(color.lastIndexOf(",")+1, color.indexOf(")")));

				tagsvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100px\" height=\"25px\" viewBox=\"0 0 100 25\" preserveAspectRatio=\"xMidYMid meet\" onload='tag_init(evt)' class=\"tagidclass"+tag_id+"\">\n";
				tagsvg+="<script type=\"application/ecmascript\"> <![CDATA[\n";
				tagsvg+="  function tag_init(evt) {\n";
				tagsvg+="    var svgDocument = evt.target;\n";
				tagsvg+="    var tt = svgDocument.getElementsByTagName('text')[0];\n";
				tagsvg+="    while (tt.getBBox().width>75) {\n";
				tagsvg+="      var sz = tt.getAttribute('font-size');\n";
				tagsvg+="      if (sz<=1) {break;}\n";
				tagsvg+="      sz--;\n";
				tagsvg+="      tt.setAttribute('font-size', sz);\n";
				tagsvg+="    }\n";
				tagsvg+="  }\n";
				tagsvg+="]]> </script>\n";
				if (class_name.equals("Point person")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 A 12.5 12.5 0 0 0 100 25 L 0 25 A 12.5 12.5 0 0 0 0 0 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Disease association")) {
					tagsvg+="<path  d=\"M 0 12 L 10 0 L 90 0 L 100 12 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Pathway")) {
					tagsvg+="<path  d=\"M 0 0 L 90 0 L 90 8 L 100 8 L 100 17 L 90 17 L 90 25 L 0 25 L 0 17 L 10 17 L 10 8 L 0 8 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				} else if (class_name.equals("Publication")) {
					tagsvg+="<path  d=\"M 10 0 L 90 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				} else  if (class_name.equals("Eisai site")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				} else if (class_name.equals("Project name")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				} else if (class_name.equals("Collaboration")) {
					tagsvg+="<path  d=\"M 10 0 L 100 0 L 90 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				} else if (class_name.equals("Statistic")) {
					tagsvg+="<path  d=\"M 12.5 0 L 87.5 0 A 12.5 12.5 0 0 1 87.5 25 L 12.5 25 A 12.5 12.5 0 0 1 12.5 0 Z\" fill=\""+color+"\" stroke=\"none\">\n";
				}
				String t = "\t<title>";
				char[] da = description.toCharArray();
				if (da!=null) {
					for (int j=0; j<da.length; ++j) {
						if (da[j]=='<') t+="&lt;";
						else if (da[j]=='>') t+="&gt;";
						else if (da[j]=='&') t+="&amp;";
						else if (da[j]=='\"') t+="&quot;";
						else if (da[j]=='\'') t+="&apos;";
						else t+=da[j];
					}
				}
				t+="</title>\n";
				tagsvg+=t;
				tagsvg+="</path>\n";
				String tcolor = "white";
				if (red+green+blue>500) tcolor="black";
				tagsvg+="<text style=\"text-anchor:middle; dominant-baseline:central; alignment-baseline:central;\" font-family=\"Lato, Sans-serif\" font-weight=\"800\" font-size=\"16\" x=\"50px\" y=\"12px\" pointer-events=\"none\" fill=\""+tcolor+"\" >"+short_name+"</text>\n";
				tagsvg+="</svg>\n";
			}
			result.close();
			return tagsvg;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
		}
	}
	
	public static String TagCreatePage(String storageDir) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			int db_count = 0;
			Path p = Paths.get(storageDir);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
				for (Path entry: stream) {
					String just_gene = entry.getFileName().toString();
					just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
					String just_gene_uc = just_gene.toUpperCase();
					if (just_gene_uc.equals("_TAGS")) continue;
					db_count++;					
				}
			} catch (DirectoryIteratorException e) {
				throw e.getCause();

			}

			StringBuffer sb = new StringBuffer();
			sb.append(GetHTMLStart("TGN - Tags","",""));

			sb.append("<div id=\"edittagoverlay\" align=\"center\" class=\"akmodal\"></div>\n");
			sb.append("<div id=\"assignbytagoverlay\" align=\"center\" class=\"akmodal\"></div>\n");
			sb.append("<div id=\"assignbytgnoverlay\" align=\"center\" class=\"akmodal\"></div>\n");
			sb.append("<div id=\"pushwebreferenceoverlay\" align=\"center\" class=\"akmodal\"></div>\n");
			sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
			sb.append("<h3 style=\"text-align: center; font: 18px/27px Lato, Sans-serif;\">Existing Tags</h3>\n");
			sb.append("<table id='available_tags' data-db_count=\""+db_count+"\" class=\"display\" cellspacing=\"0\">\n");		
			sb.append("  <thead>\n");
			sb.append("  <tr>\n");
			sb.append("    <th class=\"resizable-false sorter-false\">Tag</th>\n");
			sb.append("    <th class=\"resizable-false\">Tag Class</th>\n");
			sb.append("    <th class=\"resizable-false\">Short Name</th>\n");
			sb.append("    <th class=\"resizable-false\">Long Name</th>\n");
			sb.append("    <th class=\"resizable-false\">Description</th>\n");
			sb.append("    <th class=\"resizable-false sorter-false\"></th>\n");
			sb.append("  </tr>\n");
			sb.append("  </thead>\n");
			sb.append("<tbody>\n");

			Vector<String> sns = new Vector<>();
			Vector<String> ids = new Vector<>();
			int tag_count=0;

			String dbFilePath = storageDir+"/_tags.sqlite";
			connection = GetTagDBConnection("jdbc:sqlite:"+dbFilePath);  
			statement = connection.createStatement();
			result = statement.executeQuery("select t.id as id, short_name, long_name, description, color, c.id as class_id, c.name as class_name from tag_classes c, tags t where t.tag_class_id=c.id order by short_name");
			while(result.next()){
				tag_count++;
				String tid = result.getString("id");
				String short_name = result.getString("short_name");
				sns.add(short_name);
				ids.add(tid);
				String long_name = result.getString("long_name");
				String description = result.getString("description");
				String color = result.getString("color");
				String class_id = result.getString("class_id");
				String class_name = result.getString("class_name");
				color = color.replaceAll(" ", "");
				int red = Integer.parseInt(color.substring(color.indexOf("(")+1, color.indexOf(",")));
				int green = Integer.parseInt(color.substring(color.indexOf(",")+1, color.lastIndexOf(",")));
				int blue = Integer.parseInt(color.substring(color.lastIndexOf(",")+1, color.indexOf(")")));

				String tagsvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100px\" height=\"25px\" viewBox=\"0 0 100 25\" preserveAspectRatio=\"xMidYMid meet\" onload='tag_init(evt)'>\n";
				tagsvg+="<script type=\"application/ecmascript\"> <![CDATA[\n";
				tagsvg+="  function tag_init(evt) {\n";
				tagsvg+="  var svgDocument = evt.target;\n";
				tagsvg+="  var tt = svgDocument.getElementsByTagName('text')[0];\n";
				tagsvg+="  while (tt.getBBox().width>75) {\n";
				tagsvg+="    var sz = tt.getAttribute('font-size');\n";
				tagsvg+="    if (sz<=1) {break;}\n";
				tagsvg+="    sz--;\n";
				tagsvg+="    tt.setAttribute('font-size', sz);\n";
				tagsvg+="  }\n";
				tagsvg+="}\n";
				tagsvg+="]]> </script>\n";

				if (class_name.equals("Point person")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 A 12.5 12.5 0 0 0 100 25 L 0 25 A 12.5 12.5 0 0 0 0 0 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Disease association")) {
					tagsvg+="<path  d=\"M 0 12 L 10 0 L 90 0 L 100 12 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Pathway")) {
					tagsvg+="<path  d=\"M 0 0 L 90 0 L 90 8 L 100 8 L 100 17 L 90 17 L 90 25 L 0 25 L 0 17 L 10 17 L 10 8 L 0 8 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Publication")) {
					tagsvg+="<path  d=\"M 10 0 L 90 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else  if (class_name.equals("Eisai site")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 100 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Project name")) {
					tagsvg+="<path  d=\"M 0 0 L 100 0 L 90 25 L 10 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Collaboration")) {
					tagsvg+="<path  d=\"M 10 0 L 100 0 L 90 25 L 0 25 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				} else if (class_name.equals("Statistic")) {
					tagsvg+="<path  d=\"M 12.5 0 L 87.5 0 A 12.5 12.5 0 0 1 87.5 25 L 12.5 25 A 12.5 12.5 0 0 1 12.5 0 Z\" fill=\""+color+"\" stroke=\"none\" />\n";
				}
				String tcolor = "white";
				if (red+green+blue>500) tcolor="black";
				tagsvg+="<text style=\"text-anchor:middle; dominant-baseline:central; alignment-baseline:central;\" font-family=\"Lato, Sans-serif\" font-weight=\"800\" font-size=\"16\" x=\"50px\" y=\"12px\" fill=\""+tcolor+"\" >"+short_name+"</text>\n";
				tagsvg+="</svg>\n";
				String rowclass = "aktagrowclass"+tid;
				sb.append("  <tr id=\"tagid"+tid+"\" class=\"a_tag_row\" data-tag_color=\""+color+"\" data-tag_text_color=\""+tcolor+"\">\n");
				sb.append("  <td style=\"text-align: center; vertical-align:middle;\">"+tagsvg+"</td>");
				sb.append("    <td style=\"text-align: center; vertical-align:middle;\" data-tagclass_id=\""+class_id+"\" class=\"ak_tablecell class_name\">"+StringEscapeUtils.escapeHtml4(class_name)+"</td>\n");
				sb.append("    <td style=\"text-align: center; vertical-align:middle;\" class=\"ak_tablecell short_name\">"+StringEscapeUtils.escapeHtml4(short_name)+"</td>\n");
				sb.append("    <td><textarea readonly class=\"ak_tablecell long_name\" style=\"background-color:#f5f5f5; width:100%; height:100%\" >"+StringEscapeUtils.escapeHtml4(long_name)+"</textarea></td>\n");
				sb.append("    <td><textarea readonly class=\"ak_tablecell description\" style=\"background-color:#f5f5f5; width:100%; height:100%\" >"+StringEscapeUtils.escapeHtml4(description)+"</textarea></td>\n");
				sb.append("<td style=\"vertical-align: middle;\"><button style=\"width: 40px; display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"AddEditTagOverlay("+tid+")\">Edit</button><button style=\"width: 40px; display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"DeleteTag("+tid+")\">Delete</button></td>\n");
				sb.append("  </tr>\n");
			}
			result.close();

			sb.append("</tbody>\n");
			sb.append("</table>\n");
			sb.append("<p><button style=\"display:block; margin:auto;\" class=\"ak_table_button\" id=\"tag_edit_button\" onclick=\"AddEditTagOverlay(-1)\">New Tag</button></p>\n");
			sb.append("</div>\n");		

			sb.append("<p><div style=\"width:1300px; margin: 0 auto; display: flex; justify-content: space-between;\">\n");
			String disabledstring = "";
			if (tag_count==0 || db_count==0) disabledstring = "disabled";
			sb.append("  <button style=\"display:block; margin:auto; font: 18px/27px Lato, Sans-serif !important;\" class=\"ak_table_button\" id=\"assign_by_tag_button\" onclick=\"AddAssignByTagOverlay()\" "+disabledstring+">Assign by Tag</button>\n");
			sb.append("  <button style=\"display:block; margin:auto; font: 18px/27px Lato, Sans-serif !important;\" class=\"ak_table_button\" id=\"tag_edit_button\" onclick=\"AddAssignByTGNOverlay()\" "+disabledstring+">Assign by TGN</button>\n");
			sb.append("</div></p>\n");	

			sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
			sb.append("<p><button style=\"display:block; margin:auto; font: 18px/27px Lato, Sans-serif !important;\" class=\"ak_table_button\" id=\"push_ref_button\" onclick=\"AddPushWebReferenceOverlay()\">Push Web Reference to DBs</button></p>\n");
			sb.append("</div>\n");	

			sb.append("<script type=\"text/javascript\">\n");

			sb.append("$( document ).ready(function() {\n");
			//sb.append("  var tgn_agree = getCookie(\"tgn_agree1\");\n");
			//sb.append("  if (tgn_agree==\"\") {\n");
			//sb.append("    $('.tgn_agree').show();");
			//sb.append("  }\n");			
			sb.append("  $('#available_tags').tablesorter({theme:'blue', sortList: [[0,0],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['120px','198px','148px','258px', '464px', '102px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
			sb.append("  $('#available_tags th').css('text-align','center');\n");
			sb.append("  $('#available_tags th').css('padding','0px');\n");	
			sb.append("});\n\n");
			sb.append("</script>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static String GetDBTagList(String storageDir) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			Path p = Paths.get(storageDir);
			Gson gson = new Gson();
			Hashtable<String,String> tag_lookup = new Hashtable<>();

			String dbFilePath = storageDir+"/_tags.sqlite";
			connection = GetTagDBConnection("jdbc:sqlite:"+dbFilePath);  
			statement = connection.createStatement();
			result = statement.executeQuery("select id, short_name from tags order by short_name");
			while(result.next()){
				tag_lookup.put(result.getString("id"), result.getString("short_name"));
			}
			result.close();
			statement.close();
			connection.close();

			ArrayList<String> applied_tags = new ArrayList<>();
			ArrayList<String> sorted_dbs = new ArrayList<>();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
				for (Path entry: stream) {
					String just_gene = entry.getFileName().toString();
					just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
					String just_gene_uc = just_gene.toUpperCase();
					if (just_gene_uc.equals("_TAGS")) continue;
					LinkedHashMap<String, String> singledb = new LinkedHashMap<>();
					singledb.put("db", just_gene);
					ArrayList<String> which_tags = new ArrayList<>();

					dbFilePath = storageDir+"/"+just_gene+".sqlite";
					connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);
					statement = connection.createStatement();
					statement.executeUpdate("CREATE TABLE if not exists applied_tags (tag_id TEXT NOT NULL)");
					result = statement.executeQuery("select tag_id from applied_tags");
					while(result.next()){
						which_tags.add(tag_lookup.get(result.getString("tag_id")));
					}
					result.close();
					statement.close();
					connection.close();

					singledb.put("tags", gson.toJson(which_tags, ArrayList.class));
					int insertion_index = -1;
					for (int jk=0; jk<sorted_dbs.size(); ++jk) {
						if (just_gene.compareTo(sorted_dbs.get(jk))>1) {
							insertion_index = jk;
							break;
						}
					}
					if (insertion_index==-1) {
						applied_tags.add(gson.toJson(singledb, LinkedHashMap.class));
						sorted_dbs.add(just_gene);
					}
					else {
						applied_tags.add(insertion_index, gson.toJson(singledb, LinkedHashMap.class));
						sorted_dbs.add(insertion_index, just_gene);
					}
				}
			} catch (DirectoryIteratorException e) {
				throw e.getCause();
			}
			return gson.toJson(applied_tags, ArrayList.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static String GetDBNameList(String storageDir) throws Exception {
		Path p = Paths.get(storageDir);
		Gson gson = new Gson();
		ArrayList<String> sorted_dbs = new ArrayList<>();
		//DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
			for (Path entry: stream) {
				String just_gene = entry.getFileName().toString();
				just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
				String just_gene_uc = just_gene.toUpperCase();
				if (just_gene_uc.equals("_TAGS")) continue;
				int insertion_index = -1;
				for (int jk=0; jk<sorted_dbs.size(); ++jk) {
					if (just_gene.compareTo(sorted_dbs.get(jk))>1) {
						insertion_index = jk;
						break;
					}
				}
				if (insertion_index==-1) {
					sorted_dbs.add(just_gene);
				}
				else {
					sorted_dbs.add(insertion_index, just_gene);
				}
			}
		} catch (DirectoryIteratorException e) {
			throw e.getCause();
		}
		//stream.close();
		return gson.toJson(sorted_dbs, ArrayList.class);
	}
	
	public static String GetDBList(String storageDir) throws Exception {
		Connection connection = null;
		Connection connection_tags = null;
		Statement statement_tags = null;
		Statement statement = null;
		ResultSet result = null;
		Hashtable<String, String> tag_svgs = new Hashtable<>();
		try {
			Path p = Paths.get(storageDir);
			connection_tags = GetTagDBConnection("jdbc:sqlite:"+storageDir+"/_tags.sqlite");  
			statement_tags = connection_tags.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append(GetHTMLStart("Target Gene Notebook","",""));
			sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
			
			Vector<String> all_tag_ids = new Vector<>();
			statement = connection_tags.createStatement();
			result = statement.executeQuery("select id from tags order by id");
			while(result.next()){
				String tid = result.getString(1);
				all_tag_ids.add(tid);
				tag_svgs.put(tid, GetTagSVG(tid, statement_tags));
			}
			result.close();
			statement.close();
			
			sb.append("<p><div style=\"width:1300px; margin: 0 auto; display: flex; justify-content: space-between;\">\n");
			for (int i=0; i<all_tag_ids.size(); ++i) {
				String tag_id = all_tag_ids.get(i);
				String svg = tag_svgs.get(tag_id);
				sb.append("<button style=\"padding:0px; border:1px; height: 25px; display:block; margin:auto; filter:grayscale(100%);\" data-is_selected=\"0\" class=\"a_tag_selector\" data-tag_id=\""+tag_id+"\" onclick=\"AdjustDBFilter(this)\">"+svg+"</button>\n");
				if (i%10==0 && i>0 && i<all_tag_ids.size()-1) {
					sb.append("</div></p>\n");
					sb.append("<p><div style=\"width:1300px; margin: 0 auto; display: flex; justify-content: space-between;\">\n");
				}
			}
			sb.append("</div></p>\n");
			sb.append("<p style=\"color:#000; font-size: 20px;\"></p>\n");
			sb.append("<p style=\"color:#000; font-size: 20px;\"></p>\n");
			sb.append("<h3 style=\"text-align: center; font: 18px/27px Lato, Sans-serif;\">Available Notebooks</h3>\n");
			sb.append("<table id='available_notebooks' class=\"display\" cellspacing=\"0\">\n");		
			sb.append("  <thead>\n");
			sb.append("  <tr>\n");
			sb.append("    <th class=\"resizable-false filter-match\">Gene</th>\n");
			sb.append("    <th class=\"resizable-false\">Chr</th>\n");
			sb.append("    <th class=\"resizable-false\">Mb Location</th>\n");
			sb.append("    <th class=\"resizable-false\">Neighborhood Coding Genes</th>\n");
			sb.append("    <th class=\"resizable-false\">References Status</th>\n");
			sb.append("    <th class=\"resizable-false\">Last Modified Time</th>\n");
			sb.append("    <th class=\"resizable-false filter-false sorter-false\">Tags</th>\n");
			sb.append("  </tr>\n");
			sb.append("  </thead>\n");
			sb.append("<tbody>\n");

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.{sqlite}")) {
				for (Path entry: stream) {
					BasicFileAttributes attr = Files.readAttributes(entry, BasicFileAttributes.class);
					String just_gene = entry.getFileName().toString();
					just_gene = just_gene.substring(0, just_gene.indexOf(".sqlite"));
					String just_gene_uc = just_gene.toUpperCase();
					if (just_gene_uc.equals("_TAGS")) continue;
					String chrom = "";
					String mb_loc = "";
					String neighborhood = "";
					String ur_string = "";
					Vector<String> tags = new Vector<>();

					String dbFilePath = storageDir+"/"+just_gene+".sqlite";
					connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);  
					statement = connection.createStatement();
					result = statement.executeQuery("select chromosome from reference_genomes");
					while(result.next()){
						chrom = result.getString("chromosome");
					}
					result.close();
					result = statement.executeQuery("select symbol, exon_starts_1based, exon_ends_1based, is_gene_target, coding_start_1based from transcript_annotations order by symbol");
					while(result.next()) {
						int igt = result.getInt("is_gene_target");
						String symbol = result.getString("symbol");
						if (igt==1) {
							String es = result.getString("exon_starts_1based");
							String ee = result.getString("exon_ends_1based");
							String[] esa = es.split(" ");
							String[] eea = ee.split(" ");
							int num_exons = esa.length;
							Vector<Integer> exon_starts = new Vector<>();
							Vector<Integer> exon_ends = new Vector<>();
							for (int i=0; i<num_exons; ++i) {
								exon_starts.add(Integer.valueOf(esa[i]));
								exon_ends.add(Integer.valueOf(eea[i]));
							}
							int lowest = exon_starts.get(0);
							int highest = exon_ends.get(exon_ends.size()-1);
							if (lowest>highest) {
								lowest = exon_starts.get(exon_starts.size()-1);
								highest = exon_ends.get(0);
							}
							double avg = (lowest+highest)/2.0;
							mb_loc = String.format("%.1f", avg/1000000.0);
						} else {
							int coding_start = result.getInt("coding_start_1based");
							if (result.wasNull()) coding_start = -1;	
							if (coding_start!=-1) {
								if (!neighborhood.equals("")) {
									neighborhood+=" ";
								}
								neighborhood+=symbol;
							}
						}
					}
					result.close();
					statement.executeUpdate("CREATE TABLE if not exists applied_tags (tag_id TEXT NOT NULL)");
					result = statement.executeQuery("select tag_id from applied_tags order by tag_id");
					while(result.next()) {
						String tid = result.getString("tag_id");
						if (!tag_svgs.containsKey(tid)) {
							tag_svgs.put(tid, GetTagSVG(tid, statement_tags));
						}
						tags.add(tag_svgs.get(tid));
					}
					result.close();
					int unreviewed_reference_count = 0;
					int total_reference_count = 0;
					result = statement.executeQuery("select has_been_reviewed from source_documents");
					while (result.next()) {
						int hbr = result.getInt("has_been_reviewed");
						total_reference_count++;
						if (hbr==0) unreviewed_reference_count++;		
					}
					result.close();
					ur_string = unreviewed_reference_count+" out of "+total_reference_count+" not reviewed";
					statement.close();
					connection.close();

					sb.append("  <tr>\n");
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"./"+just_gene+"\" target=\"_tgn_"+just_gene+"\">"+just_gene_uc+"</a></td>\n");	   
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(chrom))+"</td>\n");
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(mb_loc))+"</td>\n");
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(neighborhood))+"</td>\n");
					sb.append("    <td id=\"unrev_"+just_gene_uc+"\" style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(ur_string))+"</td>\n");
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(attr.lastModifiedTime().toString())+"</td>\n");

					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">\n");
					for (int k=0; k<tags.size(); ++k) {
						sb.append(tags.get(k));
					}
					sb.append("    </td>\n");
					sb.append("  </tr>\n");
				}
			} catch (DirectoryIteratorException e) {
				throw e.getCause();
			}
			sb.append("</tbody>\n");		
			sb.append("</table>\n");
			sb.append("</div>\n");	

			sb.append("<script type=\"text/javascript\">\n");

			sb.append("$( document ).ready(function() {\n");
			//sb.append("  var tgn_agree = getCookie(\"tgn_agree1\");\n");
			//sb.append("  if (tgn_agree==\"\") {\n");
			//sb.append("    $('.tgn_agree').show();");
			//sb.append("  }\n");			

			sb.append("  $('#available_notebooks').tablesorter({theme:'blue', headers:{5:{sorter:'text'}}, sortList: [[0,0],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['123px','50px','100px', '315px', '200px', '200px', '310px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
			//sb.append("  $('#available_notebooks').tablesorterPager({container: $(\"#pager_available\"),removeRows: false});\n");

			sb.append("  $('#available_notebooks th').css('text-align','center');\n");
			sb.append("  $('#available_notebooks th').css('padding','0px');\n");	
			sb.append("\n\nsetTimeout(UnreviewedCheckin, 300000);\n\n");	
			sb.append("});\n\n");

			sb.append("</script>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");

			statement_tags.close();
			connection_tags.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (statement_tags!=null) statement_tags.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
			try {if (connection_tags!=null) connection_tags.close();} catch (Exception ee) {}
		}
	}
	
	public static String GetTagClassList(String storageDir) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			Gson gson = new Gson();
			ArrayList<String> tableData = new ArrayList<>();
			String dbFilePath = storageDir+"/_tags.sqlite";
			connection = GetTagDBConnection("jdbc:sqlite:"+dbFilePath);  
			statement = connection.createStatement();
			result = statement.executeQuery("select id, name from tag_classes order by name");
			while(result.next()){
				LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
				rowMap.put("id", result.getString("id")); 
				rowMap.put("class_name", result.getString("name"));
				String jsonObS = gson.toJson(rowMap, LinkedHashMap.class);
				tableData.add(jsonObS);
			}
			if (tableData.size()>0) return tableData.toString();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static String GetPubList(String geneSymbol, String storageDir) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			Gson gson = new Gson();
			ArrayList<String> tableData = new ArrayList<>();
			String dbFilePath = storageDir+"/"+geneSymbol+".sqlite";
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);  
			statement = connection.createStatement();
			rs = statement.executeQuery("select id, column_display_text from source_documents order by id");
			while(rs.next()){
				LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
				rowMap.put("id", rs.getString("id")); 
				rowMap.put("column_display_text", rs.getString("column_display_text"));
				String jsonObS = gson.toJson(rowMap, LinkedHashMap.class);
				tableData.add(jsonObS);
			}
			if (tableData.size()>0) return tableData.toString();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static Vector<String> GetPubAndGeneList(String geneSymbol, String storageDir) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			Gson gson = new Gson();
			ArrayList<String> pub_data = new ArrayList<>();
			ArrayList<String> gene_data = new ArrayList<>();
			String dbFilePath = storageDir+"/"+geneSymbol+".sqlite";
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);  
			statement = connection.createStatement();
			result = statement.executeQuery("select id, column_display_text from source_documents order by id");
			while(result.next()){
				LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
				rowMap.put("id", result.getString("id")); 
				rowMap.put("column_display_text", result.getString("column_display_text"));
				String jsonObS = gson.toJson(rowMap, LinkedHashMap.class);
				pub_data.add(jsonObS);
			}
			result.close();
			result = statement.executeQuery("select id, symbol as sym from transcript_annotations where (select count(*) from transcript_annotations where symbol=sym)=1 order by symbol");
			while(result.next()){
				LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
				rowMap.put("id", result.getString("id")); 
				rowMap.put("gene_symbol", result.getString("sym"));
				String jsonObS = gson.toJson(rowMap, LinkedHashMap.class);
				gene_data.add(jsonObS);
			}
			result.close();
			statement.close();
			connection.close();
			Vector<String> toret = new Vector<>();
			toret.add(pub_data.toString());
			toret.add(gene_data.toString());			
			return toret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static void UpdateTagAssign(String storage_dir, SubmitTagAssignPostMsg stapm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+stapm.db+".sqlite");
			statement = connection.createStatement();		
			if (stapm.addif1.equals("1")) {
				statement.executeUpdate("insert into applied_tags (tag_id) values ("+stapm.tag_id+")");
			} else {
				statement.executeUpdate("delete from applied_tags where tag_id="+stapm.tag_id);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static Vector<ArrayList<Integer>> RemoveGWASRow(String gene_symbol, String storage_dir, RemoveGWASRowPostMsg rgrpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet result = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();	
			statement2 = connection.createStatement();	
			result = statement.executeQuery("select show_in_svg, marker_equivalence_set, index_variant_mapping_id from variant_mapping_gwas_results where id = "+rgrpm.gwas_db_id);
			String mes = "";
			String show_in_svg = "";
			String ivid = "";
			while(result.next()) {
				mes = result.getString("marker_equivalence_set");
				show_in_svg = result.getString("show_in_svg");
				ivid = result.getString("index_variant_mapping_id");
			}
			result.close();
			result = statement.executeQuery("select count(*) from variant_mapping_gwas_results where index_variant_mapping_id = "+ivid);
			int iv_cnt=-1;
			while(result.next()) {
				iv_cnt = result.getInt(1);
			}
			result.close();
			statement.executeUpdate("delete from variant_mapping_gwas_results where id = "+rgrpm.gwas_db_id);
			if (iv_cnt==1) {
				statement2.executeUpdate("delete from gwas_ld_credible_set_r2_values where index_variant_mapping_id = "+ivid);
			}
			connection.commit();
			Vector<ArrayList<Integer>> v = GetAllGWASOverlaps(connection, -1);
			ArrayList<Integer> svg_change_a = new ArrayList<>();
			if (show_in_svg.equals("0") || mes.equals("Unset")) svg_change_a.add(Integer.valueOf(0));
			else svg_change_a.add(Integer.valueOf(1));
			v.add(svg_change_a);
			return v;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static boolean RemoveEQTLRow(String gene_symbol, String storage_dir, RemoveEQTLRowPostMsg rerpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		ResultSet result = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			statement3 = connection.createStatement();
			result = statement.executeQuery("select show_in_svg, eqtl_marker_equivalence_set_id from variant_mapping_eqtl_results where id = "+rerpm.eqtl_db_id);
			String show_in_svg = "";
			String emesi = "";
			while(result.next()) {
				show_in_svg = result.getString("show_in_svg");
				emesi = result.getString("eqtl_marker_equivalence_set_id");
				if (result.wasNull()) emesi = "";
			}
			result.close();
			
			// the manual eQTLs won't have any credible sets (except a few hacked ones)
			if (!emesi.equals("")) {
				statement.executeUpdate("delete from eqtl_marker_equivalence_set_members where eqtl_marker_equivalence_set_id = "+rerpm.eqtl_db_id);
				statement2.executeUpdate("delete from eqtl_marker_equivalence_sets where id = "+rerpm.eqtl_db_id);
			}
			statement3.executeUpdate("delete from variant_mapping_eqtl_results where id = "+rerpm.eqtl_db_id);
			connection.commit();
			if (show_in_svg.equals("0")) return false;
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}	
			try {if (statement3!=null) statement3.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<String> DownloadFile(String gene_symbol, String storage_dir, DownloadFilePostMsg dfpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();			
			result = statement.executeQuery("select name, contents from file_documents where source_document_id = "+dfpm.sd_db_id);
			String name = "";
			String b64 = "";
			while(result.next()) {
				name = result.getString("name");
				b64 = result.getString("contents");
			}
			result.close();
			Vector<String> toret = new Vector<>();
			toret.add(name);
			toret.addElement(b64);
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}			
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<String> OkToRemoveRef(String gene_symbol, String storage_dir, OkToRemoveRefPostMsg otrrpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		ResultSet result = null;
		Vector<String> toret = new Vector<>();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();	
			statement2 = connection.createStatement();
			statement3 = connection.createStatement();
			String errors = "";
			result = statement.executeQuery("select count(*) from source_document_details where source_document_id = "+otrrpm.sd_db_id);
			String count = "";
			while(result.next()) {
				count = result.getString(1);
			}
			result.close();
			if (!count.equals("0")) {			
				if (!errors.equals("")) errors+= "\n";
				errors+="Remove details first.";
			}
			result = statement.executeQuery("select count(*) from variant_mapping_gwas_results where source_document_id = "+otrrpm.sd_db_id);
			while(result.next()) {
				count = result.getString(1);
			}
			result.close();
			if (!count.equals("0")) {			
				if (!errors.equals("")) errors+= "\n";
				errors+="Remove GWAS results first.";
			}
			result = statement.executeQuery("select count(*) from variant_mapping_eqtl_results where source_document_id = "+otrrpm.sd_db_id);
			while(result.next()) {
				count = result.getString(1);
			}
			result.close();
			if (!count.equals("0")) {			
				if (!errors.equals("")) errors+= "\n";
				errors+="Remove eQTL results first.";
			}
			toret.add(errors);
			if (errors.equals("")) {
				String db_table="";
				String db_column="";
				String table_id="";
				result = statement.executeQuery("select document_type_db_location from source_documents where id = "+otrrpm.sd_db_id);
				while (result.next()) {
					String tmp = result.getString("document_type_db_location");
					db_table = tmp.substring(0, tmp.indexOf("."));
					db_column = tmp.substring(tmp.indexOf(".")+1, tmp.indexOf(" "));
					table_id = tmp.substring(tmp.indexOf(" ")+1);
				}
				result.close();
				statement2.execute("delete from "+db_table+" where "+db_column+" = "+table_id);
				statement3.execute("delete from source_documents where id = "+otrrpm.sd_db_id);
				connection.commit();
				int unreviewed_reference_count = 0;
				int total_reference_count = 0;
				result = statement.executeQuery("select has_been_reviewed from source_documents");
				while (result.next()) {
					int hbr = result.getInt("has_been_reviewed");
					total_reference_count++;
					if (hbr==0) unreviewed_reference_count++;		
				}
				result.close();
				connection.commit();
				toret.add(String.valueOf(unreviewed_reference_count));
				toret.add(String.valueOf(total_reference_count));
			} else {
				toret.add("");
				toret.add("");
			}
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}	
			try {if (statement3!=null) statement3.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<String> SubmitNewPubmed(String gene_symbol, String storage_dir, SubmitNewPubmedPostMsg snppm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		PreparedStatement pstatement3 = null;
		ResultSet result = null;
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();	

			if (snppm.pubmed_id.equals("")) errors += "No pubmed id was provided.";
			else {
				snppm.pubmed_id = snppm.pubmed_id.trim();
				try {
					BigInteger bi = new BigInteger(snppm.pubmed_id);
					if (bi.compareTo(new BigInteger("1"))==-1) throw new Exception();
				} catch (Exception eee) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Pubmed id not of recognized format.";		
				}
			}

			if (errors.equals("")) {
				result = statement.executeQuery("select count(*) from pubmed_documents where pubmed_id = '"+snppm.pubmed_id+"'");
				while(result.next()) {
					String count = result.getString(1);
					if (!count.equals("0")) {
						errors = "Pubmed id already loaded.";
					}
				}
				result.close();
			}

			Vector<String>pubmed_entry = new Vector<>();
			if (errors.equals("")) {
				try {
					pubmed_entry = RecoverPubmed(snppm.pubmed_id);
				} catch (Exception eee) {
					errors = "Unable to recover/parse pubmed entry.";
				}
			}
			
			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");	
				toret.add("");
				toret.add("");	
				toret.add("");
				return toret;
			}

			String isql = "insert into source_documents (" +
					"document_type,"+
					"column_display_text,"+
					"outbound_link,"+
					"document_year,"+
					"has_been_reviewed,"+
					"meta_resource_id ) values (?,?,?,?,0,(select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, "pubmed");
			pstatement.setString(2, pubmed_entry.get(1)+"; "+pubmed_entry.get(0));
			String pm_linkout = "http://www.ncbi.nlm.nih.gov/pubmed/?term="+snppm.pubmed_id;
			pstatement.setString(3, pm_linkout);
			pstatement.setString(4, pubmed_entry.get(4));
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String source_documents_id = "";
			while(result.next()) {
				source_documents_id = result.getString(1);
			}
			result.close();

			String isql2 = "insert into pubmed_documents (" +
					"source_document_id,"+
					"title,"+
					"pubmed_id,"+
					"first_author,"+
					"abstract, "+
					"journal, "+
					"pub_year, "+
					"pub_month ) values (?,?,?,?,?,?,?,?)";
			pstatement2 = connection.prepareStatement(isql2);
			pstatement2.setString(1, source_documents_id);
			pstatement2.setString(2, pubmed_entry.get(0));
			pstatement2.setString(3, snppm.pubmed_id);
			pstatement2.setString(4, pubmed_entry.get(1));
			pstatement2.setString(5, pubmed_entry.get(2));
			pstatement2.setString(6, pubmed_entry.get(3));
			pstatement2.setString(7, pubmed_entry.get(4));
			pstatement2.setString(8, pubmed_entry.get(5));           
			pstatement2.executeUpdate();
			result = pstatement2.getGeneratedKeys();
			String pubmed_documents_id = "";
			while(result.next()) {
				pubmed_documents_id = result.getString(1);
			}
			result.close(); 

			pstatement3 = connection.prepareStatement("update source_documents set document_type_db_location = ? where id = ?");
			pstatement3.setString(1, "pubmed_documents.id "+pubmed_documents_id);
			pstatement3.setString(2, source_documents_id);
			pstatement3.executeUpdate();			
			connection.commit();

			int unreviewed_reference_count = 0;
			int total_reference_count = 0;
			result = statement.executeQuery("select has_been_reviewed from source_documents");
			while (result.next()) {
				int hbr = result.getInt("has_been_reviewed");
				total_reference_count++;
				if (hbr==0) unreviewed_reference_count++;		
			}
			result.close();
			
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			String rowclass = "akrefrowclass"+source_documents_id;
			int childcount=1;
			int rowspancount = childcount+1;
			sb.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
		    sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(1))+"</td>");				
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(4))+"</td>");			
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(3))+"</td>");			
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(pm_linkout)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(0))+"</a></td>");	
			sb.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; left:0; right:0; bottom:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");	
			//sb.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb.append("<td rowspan=\""+1+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb.append("</tr>");
			
			sb2.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb2.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
		    sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(1))+"</td>");				
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(4))+"</td>");			
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(3))+"</td>");			
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(pm_linkout)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(pubmed_entry.get(0))+"</a></td>");
			sb2.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb2.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb2.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb2.append("<td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb2.append("</tr>");
			
			sb2.append("<tr class=\"tablesorter-childRow\" id=\'adddetail"+source_documents_id+"\' style=\"background: #eeeeee !important;\">");			
			sb2.append("<td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+source_documents_id+"')\">Add Detail</button></div></td>");
			sb2.append("</tr>");
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(source_documents_id);
			toret.add(sb2.toString());
			toret.add(String.valueOf(unreviewed_reference_count));
			toret.add(String.valueOf(total_reference_count));
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}	
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}	
			try {if (pstatement3!=null) pstatement3.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static Vector<String> CreateNewCredibleSet(String gene_symbol, String storage_dir, CreateNewCredibleSetPostMsg cncspm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		try {
			String errors = "";
			
			cncspm.credible_set_name = cncspm.credible_set_name.trim();
			if (cncspm.credible_set_name.equals("")) errors = "No credible-set name provided.";
			if (cncspm.credible_set_name.equalsIgnoreCase("add credible set") || cncspm.credible_set_name.equalsIgnoreCase("delete credible set") || cncspm.credible_set_name.equalsIgnoreCase("unset") || cncspm.credible_set_name.equalsIgnoreCase("none")) {
				errors = "Poor credible-set name provided.";
			}
			
			cncspm.index_variant_posterior = cncspm.index_variant_posterior.trim();
			Double iv_posterior = -1.0;
			try {
				iv_posterior = Double.valueOf(cncspm.index_variant_posterior);
				if (iv_posterior<0.0 || iv_posterior>1.0) {
					if (!errors.equals("")) errors+="\n";
					errors+="No valid posterior provided for index variant.";
				}
			} catch (Exception ee) {
				if (!errors.equals("")) errors+="\n";
				errors+="No valid posterior provided for index variant.";
			}
			
			Vector<String> vm_ids = new Vector<>();
			Hashtable<String, Integer> counts = new Hashtable<>();
			
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();	
			result = statement.executeQuery("select index_variant_mapping_id from variant_mapping_gwas_results where id = "+cncspm.gwas_db_id);
			while(result.next()) {
				String id = result.getString(1);
				counts.put(id, Integer.valueOf(1));
			}
			result.close();
			
			boolean rs_problems = false;
			boolean post_problems = false;
			
			pstatement = connection.prepareStatement("select id from variant_mappings where name = ?");
			for (int i=0; i<cncspm.member_rs_numbers.size(); ++i) {
				String rsn = cncspm.member_rs_numbers.get(i).trim();
				String pos = cncspm.member_posteriors.get(i);	
				Double posterior = -1.0;
				try {
					posterior = Double.valueOf(pos.trim());
					if (posterior<0.0 || posterior>1.0) {
						post_problems = true;
					}
				} catch (Exception ee) {
					post_problems = true;
				}
				pstatement.setString(1, rsn);
				result = pstatement.executeQuery();
				int ctr=0;
				while(result.next()) {
					ctr++;
					if (ctr>1) rs_problems = true;
					String id = result.getString(1);
					if (counts.containsKey(id)) {
						rs_problems = true;
						Integer ct = counts.get(id);
						ct++;
						counts.put(id, ct);
					} else {
						counts.put(id, Integer.valueOf(1));
					}
					vm_ids.add(id);
				}
				if (ctr==0) rs_problems = true;
				result.close();	
			}
			pstatement.close();
			
			if (rs_problems) {
				if (!errors.equals("")) errors+="\n";
				errors+="Some member variants are missing or are not uniquely mapped to the region.";
			}
			
			if (post_problems) {
				if (!errors.equals("")) errors+="\n";
				errors+="Some member posteriors are missing or invalid.";
			}
			
			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");				
				return toret;
			}
			
			pstatement = connection.prepareStatement("update variant_mapping_gwas_results set credible_set_name = ?, credible_set_posterior = ? where id = ?");
			pstatement.setString(1, cncspm.credible_set_name);
			pstatement.setDouble(2, iv_posterior);
			pstatement.setString(3, cncspm.gwas_db_id);
			pstatement.executeUpdate();
			
			String isql = "insert into credible_set_members (" +
					"variant_mapping_gwas_result_id,"+
					"variant_mapping_id,"+
					"posterior,"+
					"meta_resource_id ) values (?,?,?,(select id from meta_resources where name='Manual'))";

			pstatement2 = connection.prepareStatement(isql);
			for (int i=0; i<vm_ids.size(); ++i) {
				pstatement2.setString(1, cncspm.gwas_db_id);
				pstatement2.setString(2, vm_ids.get(i));
				pstatement2.setString(3, cncspm.member_posteriors.get(i).trim());
				pstatement2.executeUpdate();
			}
			connection.commit();
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(cncspm.credible_set_name+" ("+vm_ids.size()+")");
			return toret;			
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}	
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}	
		}
	}	
	
	public static LinkedHashMap<String, String> getBioRxAbstract(String bioRxId) {
		LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
		String urlS ="http://dx.doi.org/"+bioRxId;
		//for some reason curl -L is the only thing working here since it has several redirects
		StringBuilder builder= new StringBuilder();
		try {
			ProcessBuilder pb = new ProcessBuilder("curl", "-L", urlS);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				builder.append(inputLine);
			}
		} catch(IOException e){
			System.out.println(e.getStackTrace());
		}    
		String html = builder.toString();
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Elements docElements = doc.select("meta");
		Iterator<org.jsoup.nodes.Element> docIter = docElements.iterator();
		HashMap<String, String> metaNodeMap = new HashMap<>();
		while(docIter.hasNext()){
			org.jsoup.nodes.Element docElement  = (org.jsoup.nodes.Element) docIter.next();
			String nextName  =docElement.attr("name");
			String nextContent  =docElement.attr("content");
			if (metaNodeMap.get(nextName) == null){//just the first author or first whatever
				metaNodeMap.put(nextName, nextContent);
			}
		}
		resultMap.put("title", metaNodeMap.get("citation_title").toString());
		resultMap.put("doi_id", bioRxId);
		resultMap.put("first_author", metaNodeMap.get("citation_author").toString());
		String docDate =  (String)metaNodeMap.get("article:published_time");
		resultMap.put("pub_date", docDate);
		String[] dateSplit = docDate.split("-");
		String docYear = dateSplit[0];
		resultMap.put("pub_year", docYear);
		resultMap.put("outbound_link", urlS);
		resultMap.put("publisher", metaNodeMap.get("citation_publisher").toString());
		resultMap.put("abstract", metaNodeMap.get("og:description").toString());
		if (resultMap.size() < 1){
			resultMap.put("error", "Error retrieving this doi= "+urlS); 
		}
		return resultMap;
	}

	public static Vector<String> SubmitNewBiorxiv(String gene_symbol, String storage_dir, SubmitNewBiorxivPostMsg snbpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		PreparedStatement pstatement3 = null;
		ResultSet result = null;
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();	

			snbpm.doi = snbpm.doi.trim();
			if (snbpm.doi.equals("")) errors += "No doi was provided.";

			if (errors.equals("")) {
				result = statement.executeQuery("select count(*) from biorxiv_documents where doi_id = '"+snbpm.doi+"'");
				while(result.next()) {
					String count = result.getString(1);
					if (!count.equals("0")) {
						errors = "bioRxiv doi already loaded.";
					}
				}
				result.close();
			}

			LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
			if (errors.equals("")) {
				lhm = getBioRxAbstract(snbpm.doi);
				if (lhm.containsKey("error")) {
					errors = "Unable to recover/parse bioRxiv entry.";
				}
			}

			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");
				toret.add("");
				toret.add("");
				toret.add("");
				return toret;
			}

			String isql = "insert into source_documents (" +
					"document_type,"+
					"column_display_text,"+
					"outbound_link,"+
					"document_year,"+
					"has_been_reviewed,"+
					"meta_resource_id ) values (?,?,?,?,0,(select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, "biorxiv");
			pstatement.setString(2, lhm.get("first_author")+"; "+lhm.get("title"));
			pstatement.setString(3, lhm.get("outbound_link"));
			pstatement.setString(4, lhm.get("pub_year"));
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String source_documents_id = "";
			while(result.next()) {
				source_documents_id = result.getString(1);
			}
			result.close();

			String isql2 = "insert into biorxiv_documents (" +
					"source_document_id,"+
					"title,"+
					"doi_id,"+
					"first_author,"+
					"abstract, "+
					"publisher, "+
					"pub_year, "+
					"pub_date ) values (?,?,?,?,?,?,?,?)";
			pstatement2 = connection.prepareStatement(isql2);
			pstatement2.setString(1, source_documents_id);
			pstatement2.setString(2, lhm.get("title"));
			pstatement2.setString(3, lhm.get("doi_id"));
			pstatement2.setString(4, lhm.get("first_author"));
			pstatement2.setString(5, lhm.get("abstract"));
			pstatement2.setString(6, lhm.get("publisher"));
			pstatement2.setString(7, lhm.get("pub_year"));
			pstatement2.setString(8, lhm.get("pub_date"));           
			pstatement2.executeUpdate();
			result = pstatement2.getGeneratedKeys();
			String biorxiv_documents_id = "";
			while(result.next()) {
				biorxiv_documents_id = result.getString(1);
			}
			result.close();

			pstatement3 = connection.prepareStatement("update source_documents set document_type_db_location = ? where id = ?");
			pstatement3.setString(1, "biorxiv_documents.id "+biorxiv_documents_id);
			pstatement3.setString(2, source_documents_id);
			pstatement3.executeUpdate();
			
			connection.commit();

			int unreviewed_reference_count = 0;
			int total_reference_count = 0;
			result = statement.executeQuery("select has_been_reviewed from source_documents");
			while (result.next()) {
				int hbr = result.getInt("has_been_reviewed");
				total_reference_count++;
				if (hbr==0) unreviewed_reference_count++;		
			}
			result.close();
			
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();

			int childcount=1;
			int rowspancount = childcount+1;
			String rowclass = "akrefrowclass"+source_documents_id;
			sb.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("first_author"))+"</td>");				
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("pub_year"))+"</td>");			
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("publisher"))+"</td>");			
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(lhm.get("outbound_link"))+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(lhm.get("title"))+"</a></td>");
			sb.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; left:0; right:0; bottom:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb.append("<td rowspan=\""+1+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb.append("</tr>");
			
			sb2.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb2.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("first_author"))+"</td>");				
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("pub_year"))+"</td>");			
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(lhm.get("publisher"))+"</td>");			
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(lhm.get("outbound_link"))+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(lhm.get("title"))+"</a></td>");
			sb2.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb2.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb2.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb2.append("<td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb2.append("</tr>");
			sb2.append("<tr class=\"tablesorter-childRow\" id=\'adddetail"+source_documents_id+"\' style=\"background: #eeeeee !important;\">");			
			sb2.append("<td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+source_documents_id+"')\">Add Detail</button></div></td>");
			sb2.append("</tr>");			
			
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(source_documents_id);
			toret.add(sb2.toString());
			toret.add(String.valueOf(unreviewed_reference_count));
			toret.add(String.valueOf(total_reference_count));
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}	
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}	
			try {if (pstatement3!=null) pstatement3.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static Vector<String> SubmitNewFile(String gene_symbol, String storage_dir, SubmitNewFilePostMsg snfpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		PreparedStatement pstatement3 = null;
		ResultSet result = null;
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			try {
				Integer i = Integer.valueOf(snfpm.file_year.trim());
				if (i<1900 || i>2050) {
					throw new Exception();
				}
			} catch (Exception eee) {
				errors += "No valid year was provided.";
			}
			
			snfpm.file_name = snfpm.file_name.trim();
			if (snfpm.file_name.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No file was provided.";	
			} else {
				pstatement = connection.prepareStatement("select count(*) from file_documents where name = ?");
				pstatement.setString(1, snfpm.file_name);
				result = pstatement.executeQuery();
				String count = "";
				while(result.next()) {
					count = result.getString(1);
				}
				result.close();
				pstatement.close();
				if (!count.equals("0")) {
					if (!errors.equals("")) errors+= "\n";
					errors+="File name already exists in database.";
				}
			}
			if (snfpm.file_contents.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No file contents were found.";
			}
			if (snfpm.file_description.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No description was provided.";	
			}
			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");	
				toret.add("");
				toret.add("");	
				toret.add("");
				return toret;
			}

			String isql = "insert into source_documents (" +
					"document_type,"+
					"column_display_text,"+
					"outbound_link,"+
					"document_year,"+
					"has_been_reviewed,"+
					"meta_resource_id ) values (?,?,?,?,0,(select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, "file");
			pstatement.setString(2, snfpm.file_name+"; "+snfpm.file_description);
			pstatement.setString(3, "");
			pstatement.setString(4, snfpm.file_year.trim());
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String source_documents_id = "";
			while(result.next()) {
				source_documents_id = result.getString(1);
			}
			result.close();

			String isql2 = "insert into file_documents (" +
					"source_document_id,"+
					"name,"+
					"description,"+
					"contents ) values (?,?,?,?)";
			pstatement2 = connection.prepareStatement(isql2);
			pstatement2.setString(1, source_documents_id);
			pstatement2.setString(2, snfpm.file_name);
			pstatement2.setString(3, snfpm.file_description);
			pstatement2.setString(4, snfpm.file_contents);           
			pstatement2.executeUpdate();
			result = pstatement2.getGeneratedKeys();
			String file_documents_id = "";
			while(result.next()) {
				file_documents_id = result.getString(1);
			}
			result.close();

			pstatement3 = connection.prepareStatement("update source_documents set document_type_db_location = ? where id = ?");
			pstatement3.setString(1, "file_documents.id "+file_documents_id);
			pstatement3.setString(2, source_documents_id);
			pstatement3.executeUpdate();

			connection.commit();
			
			statement = connection.createStatement();
			int unreviewed_reference_count = 0;
			int total_reference_count = 0;
			result = statement.executeQuery("select has_been_reviewed from source_documents");
			while (result.next()) {
				int hbr = result.getInt("has_been_reviewed");
				total_reference_count++;
				if (hbr==0) unreviewed_reference_count++;		
			}
			result.close();
			
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			String rowclass = "akrefrowclass"+source_documents_id;
			int childcount=1;
			int rowspancount = childcount+1;
			sb.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snfpm.file_year)+"</td>");			
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><div>"+StringEscapeUtils.escapeHtml4(snfpm.file_description)+"<button style=\"margin-left:10px;\" class=\"ak_table_button "+rowclass+"\" onclick=\"DownloadFile('sourcedocumentsrow"+source_documents_id+"')\" >Download</button></div></td>");		
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snfpm.file_description)+"</td>");	
			sb.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb.append("<td rowspan=\""+1+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb.append("</tr>");
	
			sb2.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb2.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snfpm.file_year)+"</td>");			
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><div>"+StringEscapeUtils.escapeHtml4(snfpm.file_description)+"<button style=\"margin-left:10px;\" class=\"ak_table_button "+rowclass+"\" onclick=\"DownloadFile('sourcedocumentsrow"+source_documents_id+"')\" >Download</button></div></td>");		
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snfpm.file_description)+"</td>");
			sb2.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb2.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb2.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb2.append("<td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb2.append("</tr>");
			sb2.append("<tr class=\"tablesorter-childRow\" id=\'adddetail"+source_documents_id+"\' style=\"background: #eeeeee !important;\">");			
			sb2.append("<td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+source_documents_id+"')\">Add Detail</button></div></td>");
			sb2.append("</tr>");		
					
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(source_documents_id);
			toret.add(sb2.toString());
			toret.add(String.valueOf(unreviewed_reference_count));
			toret.add(String.valueOf(total_reference_count));
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}	
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}	
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}	
			try {if (pstatement3!=null) pstatement3.close();} catch (Exception ee) {}	
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static String PushNewWebToDBs(String storage_dir, SubmitPushNewWebPostMsg snwpm) throws Exception {
		Connection connection = null;
		try {
			
			String errors = "";
			try {
				Integer i = Integer.valueOf(snwpm.push_year.trim());
				if (i<1900 || i>2050) {
					throw new Exception();
				}
			} catch (Exception eee) {
				errors += "No valid year was provided.";
			}
			
			snwpm.push_site = snwpm.push_site.trim();
			if (snwpm.push_site.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No site was provided.";	
			} else {
				try {
					URL path = new URL(snwpm.push_site.trim());
					URLConnection conn = path.openConnection();
				} catch (Exception eak) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Site does not appear valid.";
				}
			}
			
			if (snwpm.push_description.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No description was provided.";	
			}
			
			if (snwpm.push_dbs.size()==0) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No DBs targetted for push.";
			}
			
			if (!errors.equals("")) return errors;
			String results = "";
			for (int i=0; i<snwpm.push_dbs.size(); ++i) {
				String gene_symbol = snwpm.push_dbs.get(i);
				if (!gene_synchronize_objects.containsKey(gene_symbol)) {
					final Boolean gs_sync = true;
					gene_synchronize_objects.put(gene_symbol, gs_sync);
				}
				Boolean sy = gene_synchronize_objects.get(gene_symbol);
				synchronized(sy) {
				boolean stop_early = false;
				connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			
				PreparedStatement pstatement = connection.prepareStatement("select count(*) from web_documents where site = ?");
				pstatement.setString(1, snwpm.push_site);
				ResultSet rsq = pstatement.executeQuery();
				String count = "";
				while(rsq.next()) {
					count = rsq.getString(1);
				}
				rsq.close();
				pstatement.close();
				if (!count.equals("0")) {
					stop_early = true;
					if (!results.equals("")) results+= "\n";
					results += gene_symbol.toUpperCase()+": site already exists in database.";
				} 
				
				pstatement = connection.prepareStatement("select count(*) from source_documents where column_display_text = ?");
				pstatement.setString(1, snwpm.push_description);
				rsq = pstatement.executeQuery();
				count = "";
				while(rsq.next()) {
					count = rsq.getString(1);
				}
				rsq.close();
				pstatement.close();
				if (!count.equals("0")) {
					if (!stop_early) {
					stop_early = true;
					if (!results.equals("")) results+= "\n";
					results += gene_symbol.toUpperCase()+": reference description already exists in database.";
					}
				}

				if (stop_early) {
					connection.close();
					continue;
				}

				String isql = "insert into source_documents (" +
					"document_type,"+
					"column_display_text,"+
					"outbound_link,"+
					"document_year,"+
					"has_been_reviewed,"+
					"meta_resource_id ) values (?,?,?,?,0,(select id from meta_resources where name='Manual'))";

				PreparedStatement pstmt = connection.prepareStatement(isql);
				pstmt.setString(1, "web");
				pstmt.setString(2, snwpm.push_description);
				pstmt.setString(3, snwpm.push_site);
				pstmt.setString(4, snwpm.push_year.trim());
				pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys();
				String source_documents_id = "";
				while(rs.next()) {
					source_documents_id = rs.getString(1);
				}
				rs.close();
				pstmt.close();

				String isql2 = "insert into web_documents (" +
						"source_document_id,"+
						"site,"+
						"description ) values (?,?,?)";
				pstmt = connection.prepareStatement(isql2);
				pstmt.setString(1, source_documents_id);
				pstmt.setString(2, snwpm.push_site);
				pstmt.setString(3, snwpm.push_description);           
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				String web_documents_id = "";
				while(rs.next()) {
					web_documents_id = rs.getString(1);
				}
				rs.close();
				pstmt.close(); 

				pstmt = connection.prepareStatement("update source_documents set document_type_db_location = ? where id = ?");
				pstmt.setString(1, "web_documents.id "+web_documents_id);
				pstmt.setString(2, source_documents_id);
				pstmt.executeUpdate();
				pstmt.close();
				connection.close();
				
				if (!results.equals("")) results+= "\n";
				results += gene_symbol.toUpperCase()+": web reference added.";
				}
			}
			return results;
		} catch(SQLException e) {
			e.printStackTrace();
			if(connection != null) connection.close();
			throw new Exception(e);
		}
	}	
	
	public static Vector<String> SubmitNewWeb(String gene_symbol, String storage_dir, SubmitNewWebPostMsg snwpm) throws Exception {
		Connection connection = null;
		ResultSet result = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		PreparedStatement pstatement3 = null;
		
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);

			try {
				Integer i = Integer.valueOf(snwpm.web_year.trim());
				if (i<1900 || i>2050) {
					throw new Exception();
				}
			} catch (Exception eee) {
				errors += "No valid year was provided.";
			}
			
			snwpm.web_site = snwpm.web_site.trim();
			if (snwpm.web_site.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No site was provided.";	
			} else {
				pstatement = connection.prepareStatement("select count(*) from web_documents where site = ?");
				pstatement.setString(1, snwpm.web_site);
				result = pstatement.executeQuery();
				String count = "";
				while(result.next()) {
					count = result.getString(1);
				}
				result.close();
				pstatement.close();
				if (!count.equals("0")) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Site already exists in database.";
				} else {
					try {
						URL path = new URL(snwpm.web_site.trim());
						URLConnection conn = path.openConnection();
					} catch (Exception eak) {
						if (!errors.equals("")) errors+= "\n";
						errors+="Site does not appear valid.";
					}
				}
			}
			if (snwpm.web_description.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No description was provided.";	
			} else {
				pstatement = connection.prepareStatement("select count(*) from source_documents where column_display_text = ?");
				pstatement.setString(1, snwpm.web_description);
				result = pstatement.executeQuery();
				String count = "";
				while(result.next()) {
					count = result.getString(1);
				}
				result.close();
				pstatement.close();
				if (!count.equals("0")) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Site description already exists in database.";
				}
			}

			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");	
				toret.add("");
				toret.add("");	
				toret.add("");
				return toret;
			}

			String isql = "insert into source_documents (" +
					"document_type,"+
					"column_display_text,"+
					"outbound_link,"+
					"document_year,"+
					"has_been_reviewed,"+
					"meta_resource_id ) values (?,?,?,?,0,(select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, "web");
			pstatement.setString(2, snwpm.web_description);
			pstatement.setString(3, snwpm.web_site);
			pstatement.setString(4, snwpm.web_year.trim());
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String source_documents_id = "";
			while(result.next()) {
				source_documents_id = result.getString(1);
			}
			result.close();

			String isql2 = "insert into web_documents (" +
					"source_document_id,"+
					"site,"+
					"description ) values (?,?,?)";
			pstatement2 = connection.prepareStatement(isql2);
			pstatement2.setString(1, source_documents_id);
			pstatement2.setString(2, snwpm.web_site);
			pstatement2.setString(3, snwpm.web_description);           
			pstatement2.executeUpdate();
			result = pstatement2.getGeneratedKeys();
			String web_documents_id = "";
			while(result.next()) {
				web_documents_id = result.getString(1);
			}
			result.close();

			pstatement3 = connection.prepareStatement("update source_documents set document_type_db_location = ? where id = ?");
			pstatement3.setString(1, "web_documents.id "+web_documents_id);
			pstatement3.setString(2, source_documents_id);
			pstatement3.executeUpdate();
			
			connection.commit();

			statement = connection.createStatement();
			int unreviewed_reference_count = 0;
			int total_reference_count = 0;
			result = statement.executeQuery("select has_been_reviewed from source_documents");
			while (result.next()) {
				int hbr = result.getInt("has_been_reviewed");
				total_reference_count++;
				if (hbr==0) unreviewed_reference_count++;		
			}
			result.close();
			
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			String rowclass = "akrefrowclass"+source_documents_id;
			int childcount=1;
			int rowspancount = childcount+1;
			sb.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snwpm.web_year)+"</td>");			
			sb.append("<td class=\"ak_tablecell\" style=\"text-align: center;\"><a href=\""+StringEscapeUtils.escapeHtml4(snwpm.web_site)+"\"  target=\"_blank\">"+StringEscapeUtils.escapeHtml4(snwpm.web_site.replaceAll("/", "/\u200B"))+"</a></td>");				
			sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snwpm.web_description)+"</td>");	
			sb.append("<td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb.append("<td rowspan=\""+1+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb.append("</tr>");
	
			sb2.append("<tr id=\'sourcedocumentsrow"+source_documents_id+"\'>");
			sb2.append("<td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+source_documents_id+"\" ></td>\n");
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snwpm.web_year)+"</td>");			
			sb2.append("<td class=\"ak_tablecell\" style=\"text-align: center;\"><a href=\""+StringEscapeUtils.escapeHtml4(snwpm.web_site)+"\"  target=\"_blank\">"+StringEscapeUtils.escapeHtml4(snwpm.web_site.replaceAll("/", "/\u200B"))+"</a></td>");	
			sb2.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snwpm.web_description)+"</td>");	
			sb2.append("<td style=\"position:relative;\' ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			//sb2.append("<td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+source_documents_id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+""+"</textarea></td>\n");
			sb2.append("<td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details</button></td>");
			sb2.append("<td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+source_documents_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+source_documents_id+"')\">X</button></td>");
			sb2.append("</tr>");
			sb2.append("<tr class=\"tablesorter-childRow\" id=\'adddetail"+source_documents_id+"\' style=\"background: #eeeeee !important;\">");			
			sb2.append("<td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+source_documents_id+"')\">Add Detail</button></div></td>");
			sb2.append("</tr>");					
			
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(source_documents_id);
			toret.add(sb2.toString());
			toret.add(String.valueOf(unreviewed_reference_count));
			toret.add(String.valueOf(total_reference_count));
			return toret;
		} catch(SQLException e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}
			try {if (pstatement3!=null) pstatement3.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}		
		}
	}	

	public static Vector<String> SubmitNewGWASRow(String gene_symbol, String storage_dir, SubmitNewGWASRowPostMsg sngrpm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement2 = null;
		PreparedStatement pstatement3 = null;

		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);

			if (sngrpm.phenotype.trim().equals("")) errors += "No phenotype was provided.";
			if (sngrpm.source_db_id.equals("-1")) { // should otherwise exist in the db, though explicit check probably useful
				if (!errors.equals("")) errors+= "\n";
				errors+="No Source selected.";
			}		
			String variant_mapping_id="";
			int vm_cnt=0;				
			sngrpm.index_variant = sngrpm.index_variant.toLowerCase().trim();
			if (sngrpm.index_variant.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No index variant provided.";
			} else {
				pstatement = connection.prepareStatement("select id from variant_mappings where name = ?");
				pstatement.setString(1, sngrpm.index_variant);
				result = pstatement.executeQuery();
				while(result.next()) {
					vm_cnt++;
					variant_mapping_id = result.getString("id");
				}
				result.close();
				pstatement.close();
				if (vm_cnt==0) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Index variant not found.";
				}
				if (vm_cnt>1) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Index variant not uniquely mapped in region.";
				}
			}
			sngrpm.allele = sngrpm.allele.toUpperCase().trim();

			double pval = -1.0;
			if (sngrpm.pvalue.trim().equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No pvalue provided.";
			} else {
				try {
					pval = Double.parseDouble(sngrpm.pvalue.trim());
					if (pval<0.0 || pval >1.0) throw new Exception();
				} catch (Exception eee) {
					pval = -1.0;
					if (!errors.equals("")) errors+= "\n";
					errors+="Pvalue does not appear to be a valid number >=0.0 and <= 1.0.";
				}
			}

			double oddsratio_beta = -1.0;
			boolean or_beta_set = false;
			if (!sngrpm.or_beta.trim().equals("")) {
				or_beta_set = true;
				try {
					oddsratio_beta = Double.parseDouble(sngrpm.or_beta.trim());
				} catch (Exception eee) {
					oddsratio_beta = -1.0;
					if (!errors.equals("")) errors+= "\n";
					errors+="OR/Beta does not appear to be a valid number.";
				}
			}

			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");				
				return toret;
			}

			String isql = "insert into variant_mapping_gwas_results (" +
					"index_variant_mapping_id,"+
					"trait,"+
					"source_document_id,"+
					"pvalue,"+
					"or_beta, "+
					"allele, "+
					//"allele_matches_fstrand_variant_allele, "+
					"curator_comment, "+
					"svg_display_name, "+
					"show_in_svg, " +
					"marker_equivalence_set, " +
					"meta_resource_id, is_pqtl ) values (?,?,?,?,?,?,?,?,0,'Unset',(select id from meta_resources where name='Manual'), ?)";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, variant_mapping_id);
			pstatement.setString(2, sngrpm.phenotype);
			pstatement.setString(3, sngrpm.source_db_id);
			pstatement.setDouble(4,pval);
			if (or_beta_set) pstatement.setDouble(5, oddsratio_beta);
			else  pstatement.setString(5, "");
			pstatement.setString(6, sngrpm.allele);
			//if (allele_matches_fstrand_allele) pstmt.setInt(7, 1);
			//else pstmt.setInt(7, 0);
			pstatement.setString(7, sngrpm.comment);

			String pval_string = String.format("%.2e", Double.valueOf(pval));
			String sdn = sngrpm.phenotype +" "+ pval_string;
			String or_beta_string = "";
			if (or_beta_set) {
				BigDecimal bd1 = new BigDecimal(oddsratio_beta);
				bd1 = bd1.setScale(2, RoundingMode.HALF_UP);
				or_beta_string = bd1.toString();
				sdn+=" "+ or_beta_string;
			}
			if (!sngrpm.allele.equals("")) {
				sdn+=" "+ sngrpm.allele;
			}

			pstatement.setString(8, sdn);
			pstatement.setString(9, sngrpm.is_pqtl);
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String gwas_table_row_id = "";
			while(result.next()) {
				gwas_table_row_id = result.getString(1);
			}
			result.close();

			statement = connection.createStatement();
			result = statement.executeQuery("select count(*) from variant_mapping_gwas_results where index_variant_mapping_id = "+variant_mapping_id);
			int iv_cnt=-1;
			while(result.next()) {
				iv_cnt = result.getInt(1);
			}
			result.close();
			if (iv_cnt==1) {

				isql = "insert into gwas_ld_credible_set_r2_values (" +
						"index_variant_mapping_id,"+
						"variant_mapping_id2,"+
						"r2_value,"+
						"reference_dataset_id,"+
						"meta_resource_id, "+
						"when_created ) select variant_mapping_id1, variant_mapping_id2, r2_value, reference_dataset_id, meta_resource_id, when_created from r2_values where variant_mapping_id1 = ?";
				pstatement2 = connection.prepareStatement(isql);
				String isql2 = "insert into gwas_ld_credible_set_r2_values (" +
						"index_variant_mapping_id,"+
						"variant_mapping_id2,"+
						"r2_value,"+
						"reference_dataset_id,"+
						"meta_resource_id, "+
						"when_created ) select variant_mapping_id2, variant_mapping_id1, r2_value, reference_dataset_id, meta_resource_id, when_created from r2_values where variant_mapping_id2 = ?";
				pstatement3 = connection.prepareStatement(isql2);

				pstatement2.setString(1, variant_mapping_id);
				pstatement2.executeUpdate();
				pstatement3.setString(1, variant_mapping_id);
				pstatement3.executeUpdate();
			}

			connection.commit();
			
			Vector<String> all_ld_datasets = new Vector<>();
			result = statement.executeQuery("select name from reference_datasets where is_ld_reference_dataset=1 order by name");
			while(result.next()) {
				String nm = result.getString("name");
				all_ld_datasets.add(nm);
			}
			result.close();

			Vector<String> LDdatasets = new Vector<>();
			Vector<String> LDdatasetsHighR2Count = new Vector<>();           
			Vector<String> typed_in = new Vector<>();
			result = statement.executeQuery("select distinct d.name as name from reference_datasets d, variant_mapping_allele_frequencies f where f.variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+variant_mapping_id+") and d.id=f.reference_dataset_id");
			while (result.next()) {
				String nm = result.getString("name");
				typed_in.add(nm);
			}
			result.close();

			for (int i=0; i<all_ld_datasets.size(); ++i) {
				String ds = all_ld_datasets.get(i);
				if (typed_in.contains(ds)) {
					String cstring = "select count(*) from gwas_ld_credible_set_r2_values where reference_dataset_id = (select id from reference_datasets where name = '"+ds+"') and r2_value>=0.6 and (index_variant_mapping_id ="+variant_mapping_id+")";
					result = statement.executeQuery(cstring);
					while (result.next()) {
						String count = result.getString(1);
						LDdatasets.add("LD "+ds);
						LDdatasetsHighR2Count.add(count);
					}
					result.close();
				} else {
					LDdatasets.add("LD "+ds);
					LDdatasetsHighR2Count.add("n/a");
				}
			}
			String outbound_link="";
			String column_display_text="";
			String document_year="";
			result = statement.executeQuery("select outbound_link, column_display_text, document_year from source_documents where id = "+sngrpm.source_db_id);
			while(result.next()) {
				outbound_link = result.getString("outbound_link");
				if (result.wasNull()) outbound_link = "";
				column_display_text = result.getString("column_display_text");
				document_year = result.getString("document_year");
			}
			result.close();

			StringBuffer sb = new StringBuffer();
			String rowclass = "akgwasrowclass"+gwas_table_row_id;
			sb.append("<tr id=\'gwasrow"+gwas_table_row_id+"\'>");
			sb.append("  <td style=\"position:relative;\"><textarea class=\"ak_gwas_sdn ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+gwas_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"startgwassvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(sdn)+"</textarea></td>\n");
			sb.append("  <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sngrpm.phenotype)+"</td>\n");
			if (outbound_link.equals("")) {
				sb.append("  <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(column_display_text)+"</td>\n");	
			} else {
				sb.append("  <td class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(column_display_text)+"</a></td>\n");		
			}
			sb.append("  <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(document_year))+"</td>\n");
			//sb.append("  <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sngrpm.index_variant)+"</td>\n");
			sb.append("  <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(sngrpm.index_variant)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sngrpm.index_variant)+"</a></td>\n");
			
			sb.append("  <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sngrpm.allele)/*+allele_mod*/+"</td>\n");			
			sb.append("  <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pval_string)+"</td>\n");
			sb.append("  <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(or_beta_string)+"</td>\n");
			sb.append("  <td><select class=\"ak_mes_selector "+rowclass+"\" style=\"width:100%\" onchange=\"UpdateGWASMarkerEquivalenceSet(this)\" data-cmes=\"Unset\" data-dbid=\""+gwas_table_row_id+"\">\n");
			sb.append("    <option selected=\"selected\" value=\"Unset\">Unset</option>\n");
			sb.append("    <option value=\"None\">None</option>\n");
			for (int ij=0; ij<LDdatasets.size(); ++ij) {
				String ds = LDdatasets.get(ij);
				String dsr = ds.replace("1000GENOMES:phase_3", "1KGp3");
				String dsc = LDdatasetsHighR2Count.get(ij);
				sb.append("    <option value=\""+StringEscapeUtils.escapeHtml4(ds)+"\">"+StringEscapeUtils.escapeHtml4(dsr)+" ("+dsc+")</option>\n");
			}
			sb.append("    <option value=\"Add credible set\">Add credible set</option>\n");
			sb.append("  </select></td>\n");
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateGWASShowHide(this)\" data-dbid=\""+gwas_table_row_id+"\"></td>\n");

			sb.append("  <td style=\"position:relative;\" ><textarea class=\"ak_gwas_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+gwas_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"startgwascommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(sngrpm.comment)+"</textarea></td>\n");
			sb.append("  <td style=\"vertical-align: middle;\"><button id=\"x_gwas_"+gwas_table_row_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveGWASRow('gwasrow"+gwas_table_row_id+"')\">X</button></td>\n");
			sb.append("</tr>\n");

			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(gwas_table_row_id);
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			try {if(connection != null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}
			try {if (pstatement3!=null) pstatement3.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}			
		}
	}

	public static Vector<String> SubmitNewEQTLRow(String gene_symbol, String storage_dir, SubmitNewEQTLRowPostMsg snerpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");		
			if (snerpm.tissue.trim().equals("")) errors += "No Tissue was provided.";
			if (snerpm.db_source_id.equals("") || snerpm.db_source_id.equals("-1")) { // should otherwise exist in the db, though explicit check probably useful
				if (!errors.equals("")) errors+= "\n";
				errors+="No Source selected.";
			}		
			if (snerpm.gene_symbol.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No Gene selected.";
			}	
			String variant_mapping_id="";
			int vm_cnt=0;				
			snerpm.indexvariant = snerpm.indexvariant.toLowerCase().trim();
			if (snerpm.indexvariant.equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No index variant provided.";
			} else {
				pstatement = connection.prepareStatement("select id from variant_mappings where name = ?");
				pstatement.setString(1, snerpm.indexvariant);
				result = pstatement.executeQuery();
				while(result.next()) {
					vm_cnt++;
					variant_mapping_id = result.getString("id");
				}
				result.close();
				pstatement.close();
				if (vm_cnt==0) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Index variant not found.";
				}
				if (vm_cnt>1) {
					if (!errors.equals("")) errors+= "\n";
					errors+="Index variant not uniquely mapped in region.";
				}
			}
			snerpm.allele = snerpm.allele.toUpperCase().trim();
			double pval = -1.0;
			if (snerpm.pvalue.trim().equals("")) {
				if (!errors.equals("")) errors+= "\n";
				errors+="No pvalue provided.";
			} else {
				try {
					pval = Double.parseDouble(snerpm.pvalue.trim());
					if (pval<0.0 || pval >1.0) throw new Exception();
				} catch (Exception eee) {
					pval = -1.0;
					if (!errors.equals("")) errors+= "\n";
					errors+="Pvalue does not appear to be a valid number >=0.0 and <= 1.0.";
				}
			}
			double beta = -1.0;
			try {
				beta = Double.parseDouble(snerpm.beta.trim());
			} catch (Exception eee) {
				beta = -1.0;
				if (!errors.equals("")) errors+= "\n";
				errors+="Beta does not appear to be a valid number.";
			}

			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");				
				return toret;
			}

			String isql = "insert into variant_mapping_eqtl_results (" +
					"index_variant_mapping_id,"+
					"gene_symbol,"+
					"tissue,"+
					"source_document_id,"+
					"pvalue,"+
					"beta, "+
					"effect_allele, "+
					"curator_comment, "+
					"svg_display_name, "+
					"show_in_svg, " +
					"meta_resource_id ) values (?,?,?,?,?,?,?,?,?,0,(select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, variant_mapping_id);
			pstatement.setString(2, snerpm.gene_symbol);
			pstatement.setString(3, snerpm.tissue);
			pstatement.setString(4, snerpm.db_source_id);
			pstatement.setDouble(5, pval);
			pstatement.setDouble(6, beta);
			pstatement.setString(7, snerpm.allele);
			pstatement.setString(8, snerpm.comment);
			String pval_string = String.format("%.2e", Double.valueOf(pval));
			String sdn = "EQTL "+snerpm.gene_symbol+" "+snerpm.tissue +" "+ pval_string;
			pstatement.setString(9, sdn);
			pstatement.executeUpdate();
			result = pstatement.getGeneratedKeys();
			String eqtl_table_row_id = "";
			while(result.next()) {
				eqtl_table_row_id = result.getString(1);
			}
			result.close();
			pstatement.close();

			StringBuffer sb = new StringBuffer();
			String rowclass = "akeqtlrowclass"+eqtl_table_row_id;
			sb.append("  <tr id=\'eqtlrow"+eqtl_table_row_id+"\'>\n");
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_eqtl_sdn ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+eqtl_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"starteqtlsvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(sdn)+"</textarea></td>\n");
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snerpm.tissue)+"</td>\n");
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snerpm.gene_symbol)+"</td>\n");
				
			pstatement = connection.prepareStatement("select column_display_text, outbound_link, document_year from source_documents where id = ?");
			pstatement.setString(1, snerpm.db_source_id);
			result = pstatement.executeQuery();
			String cdt = "";
			String ol = "";
			String dy = "";
			while(result.next()) {
				cdt = result.getString(1);
				ol = result.getString(2);
				if (result.wasNull()) ol = "";
				dy = result.getString(3);
			}
			result.close();
			
			if (!ol.equals("")) sb.append("    <td class=\"ak_tablecell\"><a href=\""+ol+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(cdt)+"</a></td>\n");
			else sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(cdt)+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(dy))+"</td>\n");		
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(snerpm.indexvariant)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(snerpm.indexvariant)+"</a></td>\n");
			String pval_str = String.format("%.2e", Double.valueOf(pval));
			String beta_str = String.format("%.3f", Double.valueOf(beta));	
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pval_str)+"</td>\n");			
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(beta_str)+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(snerpm.allele)+"</td>\n");
			sb.append("    <td style=\"text-align:left;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4("None")+"</td>\n");
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateEQTLShowHide(this)\" data-dbid=\""+eqtl_table_row_id+"\"></td>\n");
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_eqtl_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+eqtl_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"starteqtlcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(snerpm.comment)+"</textarea></td>\n");
			sb.append("    <td style=\"vertical-align: middle;\"><button id=\"x_eqtl_"+eqtl_table_row_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveEQTLRow('eqtlrow"+eqtl_table_row_id+"')\">X</button></td>");
			Vector<ArrayList<Integer>> v = GetAllGWASOverlaps(connection, Integer.valueOf(eqtl_table_row_id));			
			ArrayList<Integer> vl = v.get(0);
			sb.append("    <td id=\'eqtloverlap"+eqtl_table_row_id+"\' style=\"text-align:center;\" class=\"ak_tablecell\">"+vl.get(0)+"</td>\n");
			sb.append("  </tr>\n");
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(eqtl_table_row_id);
			return toret;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (result!=null) result.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}	
		}
	}

	public static Vector<String> SubmitDetailSectionAssignment(String gene_symbol, String storage_dir, SubmitSectionAssignmentPostMsg ssapm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();	
			statement2 = connection.createStatement();	
			statement3 = connection.createStatement();				
			String source_documents_id = "";
			String old_section = "";

			rs = statement.executeQuery("select source_document_id, section_assignment from source_document_details where id = "+ssapm.detail_id);
			while(rs.next()) {
				source_documents_id = rs.getString("source_document_id");
				old_section = rs.getString("section_assignment");
			}
			rs.close();

			String new_section = ssapm.new_section;
			String old_section_source_documents_id = "";
			String new_section_source_documents_id = "";			
			if (!old_section.equals("None")) old_section_source_documents_id = old_section.toLowerCase()+"_sourcedocumentsrow"+source_documents_id;
			if (!new_section.equals("None")) new_section_source_documents_id = new_section.toLowerCase()+"_sourcedocumentsrow"+source_documents_id;
			statement.execute("update source_document_details set section_assignment = '"+ssapm.new_section+"' where id = "+ssapm.detail_id);
			SourceDocument sd = new SourceDocument();

			rs = statement.executeQuery("select id, document_type, document_year, document_type_db_location, outbound_link from source_documents where id = "+source_documents_id);
			while (rs.next()) {
				String id = rs.getString("id");
				String type = rs.getString("document_type");
				String type_db_location = rs.getString("document_type_db_location");
				String linkout = rs.getString("outbound_link");
				if (rs.wasNull()) linkout = "";
				sd.outbound_link = linkout;
				String document_year = rs.getString("document_year");
				sd.id = id;
				if (type.equals("pubmed")) {
					String p_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, title, pubmed_id, first_author, journal, pub_year from pubmed_documents where id = "+p_id);
					while (rs2.next()) {
						String pubmed_document_id = rs2.getString("id");
						String title = rs2.getString("title");
						String pubmed_id = rs2.getString("pubmed_id");
						String first_author = rs2.getString("first_author");
						String journal = rs2.getString("journal");
						String pub_year = rs2.getString("pub_year");
						PubmedDocument pd = new PubmedDocument();
						pd.first_author = first_author;
						pd.title = title;
						pd.journal = journal;
						pd.pubmed_id = pubmed_id;
						pd.id = pubmed_document_id;
						pd.pub_year = pub_year;
						sd.pmdoc = pd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("biorxiv")) {
					String b_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, title, doi_id, first_author, publisher, pub_year, pub_date from biorxiv_documents where id = "+b_id);
					while (rs2.next()) {
						String biorxiv_document_id = rs2.getString("id");
						String title = rs2.getString("title");
						String doi_id = rs2.getString("doi_id");
						String first_author = rs2.getString("first_author");
						String publisher = rs2.getString("publisher");
						String pub_year = rs2.getString("pub_year");
						String pub_date = rs2.getString("pub_date");						
						BiorxivDocument bd = new BiorxivDocument();
						bd.first_author = first_author;
						bd.title = title;
						bd.publisher = publisher;
						bd.doi_id = doi_id;
						bd.id = biorxiv_document_id;
						bd.pub_year = pub_year;
						bd.pub_date = pub_date;
						sd.bdoc = bd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("file")) {
					String f_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, name, description, contents from file_documents where id = "+f_id);
					while (rs2.next()) {
						String file_document_id = rs2.getString("id");
						String name = rs2.getString("name");
						String description = rs2.getString("description");
						String contents = rs2.getString("contents");
						FileDocument fd = new FileDocument();
						fd.contents = contents;
						fd.name = name;
						fd.id = file_document_id;
						fd.description = description;
						fd.year = document_year;
						sd.fdoc = fd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("web")) {
					String w_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, site, description from web_documents where id = "+w_id);
					while (rs2.next()) {
						String web_document_id = rs2.getString("id");
						String site = rs2.getString("site");
						String description = rs2.getString("description");
						WebDocument wd = new WebDocument();
						wd.site = site;
						wd.id = web_document_id;
						wd.description = description;
						wd.year = document_year;
						sd.wdoc = wd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				}
			}
			rs.close();

			String new_section_revised_rows = "";
			String old_section_revised_rows = "";
			int new_section_count = 0;
			int old_section_count = 0;
			for (int j=0; j<sd.details.size(); ++j) {
				if (sd.details.get(j).section_assignment.equals(new_section)) new_section_count++;
				if (sd.details.get(j).section_assignment.equals(old_section)) old_section_count++;					
			}

			if (new_section_count>0) {
				StringBuffer sb = new StringBuffer();
				sb.append("<tr id=\'"+new_section_source_documents_id+"\'>");
				if (sd.pmdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.journal)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.title)+"</a></td>");
				} else if (sd.bdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.publisher)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.title)+"</a></td>");									
				} else if (sd.fdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.name)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.description)+"</td>");
				} else if (sd.wdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.year)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.outbound_link.replaceAll("/", "/\u200B"))+"</a></td>");
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.description)+"</td>");
				}
				sb.append("</tr>");
				for (int j=0; j<sd.details.size(); ++j) {
					Detail d = sd.details.get(j);
					if (d.section_assignment.equals(new_section)) {
						String rowclass = "akrefrowclass"+sd.id+" akdetailrowclass"+d.id;
						sb.append("<tr style=\"background: #eeeeee !important;\" id='"+new_section.toLowerCase()+"_detail"+d.id+"' class=\"tablesorter-childRow\" >");
						sb.append("<td colspan=\"4\" style=\"vertical-align: middle\">");
						sb.append("<div style=\"width:1000px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">");
						sb.append("<textarea id='detailrowsection"+d.id+"' data-fromwhere=\"2\" data-dbid=\""+d.id+"\" class=\"ak_tablecell ak_det_desc "+rowclass+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 70px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" data-curval=\""+StringEscapeUtils.escapeHtml4(d.description)+"\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(d.description)+"</textarea>");
						sb.append("<a class=\""+rowclass+"\" style=\"display: inline-block;\" href=\""+d.b64_contents+"\" data-lightbox=\""+new_section.toLowerCase()+"_detail"+d.id+"\">");
						sb.append("<img class=\"contain_img "+rowclass+"\" src=\""+d.b64_contents+"\" width=\"300\" height=\"200\"></img></a>");
						sb.append("</div></td></tr>");
					}
				}
				new_section_revised_rows = sb.toString();
			}

			if (old_section_count>0) {
				StringBuffer sb = new StringBuffer();
				sb.append("<tr id=\'"+old_section_source_documents_id+"\'>");
				if (sd.pmdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.journal)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.title)+"</a></td>");
				} else if (sd.bdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.publisher)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.title)+"</a></td>");
				} else if (sd.fdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>\n");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.name)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.description)+"</td>");
				} else if (sd.wdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.year)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.outbound_link.replaceAll("/", "/\u200B"))+"</a></td>");
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.description)+"</td>");
				}
				sb.append("</tr>");
				for (int j=0; j<sd.details.size(); ++j) {
					Detail d = sd.details.get(j);
					if (d.section_assignment.equals(old_section)) {
						String rowclass = "akrefrowclass"+sd.id+" akdetailrowclass"+d.id;
						sb.append("<tr style=\"background: #eeeeee !important;\" id='"+old_section.toLowerCase()+"_detail"+d.id+"' class=\"tablesorter-childRow\" >");
						sb.append("<td colspan=\"4\" style=\"vertical-align: middle\">");
						sb.append("<div style=\"width:1000px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">");
						sb.append("<textarea id='detailrowsection"+d.id+"' data-fromwhere=\"2\" data-dbid=\""+d.id+"\" class=\"ak_tablecell ak_det_desc "+rowclass+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 70px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" data-curval=\""+StringEscapeUtils.escapeHtml4(d.description)+"\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(d.description)+"</textarea>");
						sb.append("<a class=\""+rowclass+"\" style=\"display: inline-block;\" href=\""+d.b64_contents+"\" data-lightbox=\""+old_section.toLowerCase()+"_detail"+d.id+"\">");
						sb.append("<img class=\"contain_img "+rowclass+"\" src=\""+d.b64_contents+"\" width=\"300\" height=\"200\"></img></a>");
						sb.append("</div></td></tr>");
					}
				}
				old_section_revised_rows = sb.toString();
			}
			Vector<String> toret = new Vector<>();
			toret.add(old_section);
			toret.add(old_section_source_documents_id);
			toret.add(old_section_revised_rows);
			toret.add(new_section);
			toret.add(new_section_source_documents_id);
			toret.add(new_section_revised_rows);			
			return toret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (rs2!=null) rs2.close();} catch (Exception ee) {}
			try {if (rs3!=null) rs3.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}
			try {if (statement3!=null) statement3.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static Vector<String> RemoveDetail(String gene_symbol, String storage_dir, RemoveDetailPostMsg rdpm) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();	
			statement2 = connection.createStatement();	
			statement3 = connection.createStatement();				
			String source_documents_id = "";
			String old_section = "";		
			rs = statement.executeQuery("select source_document_id, section_assignment from source_document_details where id = "+rdpm.detail_id);
			while(rs.next()) {
				source_documents_id = rs.getString("source_document_id");
				old_section = rs.getString("section_assignment");
			}
			rs.close();

			String old_section_source_documents_id = "";		
			if (!old_section.equals("None")) old_section_source_documents_id = old_section.toLowerCase()+"_sourcedocumentsrow"+source_documents_id;
			statement.execute("delete from source_document_details where id = "+rdpm.detail_id);
			SourceDocument sd = new SourceDocument();

			rs = statement.executeQuery("select id, document_type, document_year, document_type_db_location, outbound_link from source_documents where id = "+source_documents_id);
			while (rs.next()) {
				String id = rs.getString("id");
				String type = rs.getString("document_type");
				String type_db_location = rs.getString("document_type_db_location");
				String linkout = rs.getString("outbound_link");
				if (rs.wasNull()) linkout = "";
				sd.outbound_link = linkout;
				String document_year = rs.getString("document_year");
				sd.id = id;
				if (type.equals("pubmed")) {
					String p_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, title, pubmed_id, first_author, journal, pub_year from pubmed_documents where id = "+p_id);
					while (rs2.next()) {
						String pubmed_document_id = rs2.getString("id");
						String title = rs2.getString("title");
						String pubmed_id = rs2.getString("pubmed_id");
						String first_author = rs2.getString("first_author");
						String journal = rs2.getString("journal");
						String pub_year = rs2.getString("pub_year");
						PubmedDocument pd = new PubmedDocument();
						pd.first_author = first_author;
						pd.title = title;
						pd.journal = journal;
						pd.pubmed_id = pubmed_id;
						pd.id = pubmed_document_id;
						pd.pub_year = pub_year;
						sd.pmdoc = pd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("biorxiv")) {
					String b_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, title, doi_id, first_author, publisher, pub_year, pub_date from biorxiv_documents where id = "+b_id);
					while (rs2.next()) {
						String biorxiv_document_id = rs2.getString("id");
						String title = rs2.getString("title");
						String doi_id = rs2.getString("doi_id");
						String first_author = rs2.getString("first_author");
						String publisher = rs2.getString("publisher");
						String pub_year = rs2.getString("pub_year");
						String pub_date = rs2.getString("pub_date");						
						BiorxivDocument bd = new BiorxivDocument();
						bd.first_author = first_author;
						bd.title = title;
						bd.publisher = publisher;
						bd.doi_id = doi_id;
						bd.id = biorxiv_document_id;
						bd.pub_year = pub_year;
						bd.pub_date = pub_date;
						sd.bdoc = bd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("file")) {
					String f_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, name, description, contents from file_documents where id = "+f_id);
					while (rs2.next()) {
						String file_document_id = rs2.getString("id");
						String name = rs2.getString("name");
						String description = rs2.getString("description");
						String contents = rs2.getString("contents");
						FileDocument fd = new FileDocument();
						fd.contents = contents;
						fd.name = name;
						fd.id = file_document_id;
						fd.description = description;
						fd.year = document_year;
						sd.fdoc = fd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				} else if (type.equals("web")) {
					String w_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
					rs2 = statement2.executeQuery("select id, site, description from web_documents where id = "+w_id);
					while (rs2.next()) {
						String web_document_id = rs2.getString("id");
						String site = rs2.getString("site");
						String description = rs2.getString("description");
						WebDocument wd = new WebDocument();
						wd.site = site;
						wd.id = web_document_id;
						wd.description = description;
						wd.year = document_year;
						sd.wdoc = wd;
						rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
						while (rs3.next()) {
							Detail d = new Detail();
							d.id = rs3.getString("id");
							d.b64_contents = rs3.getString("detail_contents");
							d.section_assignment = rs3.getString("section_assignment");
							if (rs3.wasNull()) d.section_assignment = "";
							d.description = rs3.getString("description");
							sd.details.add(d);
						}
						rs3.close();
					}
					rs2.close();
				}
			}
			rs.close();

			String old_section_revised_rows = "";
			int old_section_count = 0;
			for (int j=0; j<sd.details.size(); ++j) {
				if (sd.details.get(j).section_assignment.equals(old_section)) old_section_count++;					
			}

			if (old_section_count>0) {
				StringBuffer sb = new StringBuffer();
				sb.append("<tr id=\'"+old_section_source_documents_id+"\'>");
				if (sd.pmdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.journal)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.title)+"</a></td>");
				} else if (sd.bdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.first_author)+"</td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.pub_year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.publisher)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.title)+"</a></td>");
				} else if (sd.fdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.year)+"</td>");			
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.name)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.description)+"</td>");
				} else if (sd.wdoc!=null) {
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"></td>");				
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.year)+"</td>");		
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.outbound_link.replaceAll("/", "/\u200B"))+"</a></td>");
					sb.append("<td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.description)+"</td>");
				}
				sb.append("</tr>");
				for (int j=0; j<sd.details.size(); ++j) {
					Detail d = sd.details.get(j);
					if (d.section_assignment.equals(old_section)) {
						String rowclass = "akrefrowclass"+sd.id+" akdetailrowclass"+d.id;
						sb.append("<tr style=\"background: #eeeeee !important;\" id='"+old_section.toLowerCase()+"_detail"+d.id+"' class=\"tablesorter-childRow\">");
						sb.append("<td colspan=\"4\" style=\"vertical-align: middle\">");
						sb.append("<div style=\"width:1000px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">");
						sb.append("<textarea id='detailrowsection"+d.id+"' data-fromwhere=\"2\" data-dbid=\""+d.id+"\" class=\"ak_tablecell ak_det_desc "+rowclass+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 70px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" data-curval=\""+StringEscapeUtils.escapeHtml4(d.description)+"\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(d.description)+"</textarea>");
						sb.append("<a class=\""+rowclass+"\" style=\"display: inline-block;\" href=\""+d.b64_contents+"\" data-lightbox=\""+old_section.toLowerCase()+"_detail"+d.id+"\">");
						sb.append("<img class=\"contain_img "+rowclass+"\" src=\""+d.b64_contents+"\" width=\"300\" height=\"200\"></img></a>");
						sb.append("</div></td></tr>");
					}
				}
				old_section_revised_rows = sb.toString();
			}
			Vector<String> toret = new Vector<>();
			toret.add(old_section);
			toret.add(old_section_source_documents_id);
			toret.add(old_section_revised_rows);			
			toret.add(source_documents_id);
			return toret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (rs2!=null) rs2.close();} catch (Exception ee) {}
			try {if (rs3!=null) rs3.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}
			try {if (statement3!=null) statement3.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static Vector<String> SubmitNewDetail(String gene_symbol, String storage_dir, SubmitNewDetailPostMsg sndpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		try {
			String errors = "";
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");		
			if (sndpm.desc.trim().equals("")) errors += "No description was provided.";
			if (sndpm.img_b64.equals("")) { 
				if (!errors.equals("")) errors+= "\n";
				errors+="No image was provided.";
			}				
			if (!errors.equals("")) {
				Vector<String> toret = new Vector<>();
				toret.add(errors);
				toret.add("");
				toret.add("");				
				return toret;
			}

			String isql = "insert into source_document_details (" +
					"source_document_id,"+
					"description, "+
					"detail_contents, "+
					"section_assignment, "+                   
					"meta_resource_id ) values (?,?,?,'None', (select id from meta_resources where name='Manual'))";

			pstatement = connection.prepareStatement(isql);
			pstatement.setString(1, sndpm.source_db_id);
			pstatement.setString(2, sndpm.desc.trim());
			pstatement.setString(3,sndpm.img_b64); // clob/blob handling would be better?
			pstatement.executeUpdate();
			rs = pstatement.getGeneratedKeys();
			String detail_table_row_id = "";
			while(rs.next()) {
				detail_table_row_id = rs.getString(1);
			}
			rs.close();
			StringBuffer sb = new StringBuffer();
			String rowclass = "akrefrowclass"+sndpm.source_db_id;
			String drowclass = "akdetailrowclass"+detail_table_row_id;
			sb.append("  <tr style=\"background: #eeeeee !important;\" id='detail"+detail_table_row_id+"' class=\"tablesorter-childRow\" >\n");
			sb.append("    <td colspan=\"7\" style=\"vertical-align: middle\">\n");				
			sb.append("      <div style=\"width:1175px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">\n");
			sb.append("          <section style=\"width:150px; height:100px; display: inline-block; vertical-align: top; margin-top: 80px;\">");				
			sb.append("            <label style=\"display: block; font-size: 12px;\" >Section Assignment</label>");				
			sb.append("            <select style=\"text-align: left; display: block; width:140px;\" class=\"ak_detail_section_selector "+rowclass+" "+drowclass+"\" data-csection=\"None\" onchange=\"UpdateSection(this)\" data-dbid=\""+detail_table_row_id+"\">\n");
			sb.append("              <option selected=\"selected\" value=\"None\">None</option>\n");
			sb.append("              <option value=\"Association\">Association</option>\n");
			sb.append("              <option value=\"Clinical\">Clinical</option>\n");
			sb.append("              <option value=\"Expression\">Expression</option>\n");
			sb.append("              <option value=\"Functional\">Functional</option>\n");
			sb.append("              <option value=\"PQTL\">Protein</option>\n");
			sb.append("              <option value=\"Protein\">Protein</option>\n");
			sb.append("            </select>\n");
			sb.append("          </section>");		
			sb.append("          <textarea id='detailrowsection"+detail_table_row_id+"' data-fromwhere=\"2\" data-dbid=\""+detail_table_row_id+"\" class=\"ak_tablecell ak_det_desc "+rowclass+" "+drowclass+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 20px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" data-curval=\""+StringEscapeUtils.escapeHtml4(sndpm.desc)+"\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(sndpm.desc)+"</textarea>\n");
			sb.append("          <a style=\"display: inline-block;\" class=\""+rowclass+" "+drowclass+"\" href=\""+sndpm.img_b64+"\" data-lightbox=\"detail"+detail_table_row_id+"\">\n");
			sb.append("          <img class=\"contain_img "+rowclass+" "+drowclass+"\" src=\""+sndpm.img_b64+"\" width=\"300\" height=\"200\"></img></a>\n");
			sb.append("          <button id=\"x_detail_"+detail_table_row_id+"\" type=\"button\" class=\"ak_table_button "+rowclass+" "+drowclass+"\" style=\"margin-left:50px;display: inline-block; vertical-align: top; margin-top:90px;\" onclick=\"RemoveDetail('detail"+detail_table_row_id+"')\">Remove</button>\n");
			sb.append("      </div>\n");
			sb.append("    </td>\n");
			sb.append("  </tr>\n");				

			sb.append("<tr style=\"background: #eeeeee !important;\" id='adddetail"+sndpm.source_db_id+"' class=\"tablesorter-childRow\"><td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+sndpm.source_db_id+"')\">Add Detail</button></div></td></tr>\n");
			Vector<String> toret = new Vector<>();
			toret.add("");
			toret.add(sb.toString());
			toret.add(detail_table_row_id);
			return toret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}	

	public static void UpdateGWASComment(String gene_symbol, String storage_dir, UpdateGWASCommentPostMsg ugcpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update variant_mapping_gwas_results set curator_comment=? where id=?");
			pstatement.setString(1, ugcpm.gwas_comment);
			pstatement.setInt(2, ugcpm.gwas_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static void UpdateGenePhenComment(String gene_symbol, String storage_dir, UpdateGenePhenCommentPostMsg ugpcpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update gene_phenotype_associations set curator_comment=? where id=?");
			pstatement.setString(1, ugpcpm.genephen_comment);
			pstatement.setInt(2, ugpcpm.genephen_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static void UpdateDetailComment(String gene_symbol, String storage_dir, UpdateDetailCommentPostMsg udcpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update source_document_details set description=? where id=?");
			pstatement.setString(1, udcpm.comment);
			pstatement.setInt(2, udcpm.detail_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static void UpdateEQTLComment(String gene_symbol, String storage_dir, UpdateEQTLCommentPostMsg uecpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update variant_mapping_eqtl_results set curator_comment=? where id=?");
			pstatement.setString(1, uecpm.eqtl_comment);
			pstatement.setInt(2, uecpm.eqtl_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static void UpdateSourceComment(String gene_symbol, String storage_dir, UpdateSourceCommentPostMsg uscpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update source_documents set curator_comment=? where id=?");
			pstatement.setString(1, uscpm.source_comment);
			pstatement.setInt(2, uscpm.source_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static Vector<String> UpdateSourceReview(String gene_symbol, String storage_dir, UpdateSourceReviewPostMsg usrpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		Statement statement = null;
		ResultSet rs = null;
		/*
		  n.b. this won't fail if row has been deleted, which should be ok
		 */
		Vector<String> toret = new Vector<>();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update source_documents set has_been_reviewed=? where id=?");
			if (usrpm.reviewed_or_not.equals("Reviewed")) pstatement.setString(1, "1");
			else pstatement.setString(1, "0");
			pstatement.setInt(2, usrpm.source_db_id);
			pstatement.executeUpdate();	
			int unreviewed_reference_count = 0;
			int total_reference_count = 0;
			statement = connection.createStatement();
			rs = statement.executeQuery("select has_been_reviewed from source_documents");
			while (rs.next()) {
				int hbr = rs.getInt("has_been_reviewed");
				total_reference_count++;
				if (hbr==0) unreviewed_reference_count++;		
			}
			rs.close();
			toret.add(String.valueOf(unreviewed_reference_count));
			toret.add(String.valueOf(total_reference_count));
			return toret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static boolean UpdateGWASSVGDisplayName(String gene_symbol, String storage_dir, UpdateGWASSVGDisplayNamePostMsg usdnpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		Statement statement = null;
		ResultSet rs = null;
		/*
		 In the unlikely even that the gwas row disappeared from the db before this fxn got executed, the fxn won't fail
		 and will return values consistent with the svg being remade.
		 
		 That seems like a reasonable outcome, though more sophisticated handling could be desirable, for sure.
		 */
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();
			rs = statement.executeQuery("select show_in_svg, marker_equivalence_set from variant_mapping_gwas_results where id = "+usdnpm.gwas_db_id);
			String show_in_svg = "";
			String mes = "";
			while(rs.next()) {
				show_in_svg = rs.getString("show_in_svg");
				mes = rs.getString("marker_equivalence_set");
			}
			rs.close();
			pstatement = connection.prepareStatement("update variant_mapping_gwas_results set svg_display_name=? where id=?");
			pstatement.setString(1, usdnpm.svgdisplayname);
			pstatement.setInt(2, usdnpm.gwas_db_id);
			pstatement.executeUpdate();
			if (show_in_svg.equals("0") || mes.equals("Unset")) return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}  

	public static boolean UpdateEQTLSVGDisplayName(String gene_symbol, String storage_dir, UpdateEQTLSVGDisplayNamePostMsg usdnpm) throws Exception {
		/*
		  See comment for previous function
		 */
		Connection connection = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();
			rs = statement.executeQuery("select show_in_svg from variant_mapping_eqtl_results where id = "+usdnpm.eqtl_db_id);
			String show = "";
			while(rs.next()) {
				show = rs.getString("show_in_svg");
			}
			rs.close();
			pstatement = connection.prepareStatement("update variant_mapping_eqtl_results set svg_display_name=? where id=?");
			pstatement.setString(1, usdnpm.svgdisplayname);
			pstatement.setInt(2, usdnpm.eqtl_db_id);
			pstatement.executeUpdate();
			if (show.equals("0")) return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}  

	public static void UpdateSummary(String gene_symbol, String storage_dir, UpdateSummaryPostMsg uspm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			pstatement = connection.prepareStatement("update project_summary set summary=?");
			pstatement.setString(1, uspm.summary);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<ArrayList<Integer>> UpdateGWASMarkerEquivalenceSet(String gene_symbol, String storage_dir, UpdateGWASMarkerEquivalenceSetPostMsg umespm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();
			rs = statement.executeQuery("select show_in_svg from variant_mapping_gwas_results where id = "+umespm.gwas_db_id);
			String show = "";
			while(rs.next()) {
				show = rs.getString("show_in_svg");
			}
			rs.close();
			pstatement = connection.prepareStatement("update variant_mapping_gwas_results set marker_equivalence_set = ? where id = ?");
			pstatement.setString(1, umespm.marker_equivalence_set);
			pstatement.setInt(2, umespm.gwas_db_id);
			pstatement.executeUpdate();
			
			Vector<ArrayList<Integer>> v = GetAllGWASOverlaps(connection, -1);
			ArrayList<Integer> svg_change_a = new ArrayList<>();
			if (show.equals("1")) svg_change_a.add(Integer.valueOf(1));
			else svg_change_a.add(Integer.valueOf(0));
			v.add(svg_change_a);
			return v;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<String> RecoverPubmed(String pubmedId) throws Exception {

		String pubmedUrl ="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+pubmedId+"&retmode=xml";
		URL obj = new URL(pubmedUrl);
		HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		Vector<String> lines = new Vector<>();
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();

		String title = "";
		String first_author = "";
		String abstrct = "";
		String journal_abrev_title = "";
		String pub_month = "";
		String pub_year = "";
		boolean bad = false;

		int li = 0;
		while (li<lines.size()) {
			String i = lines.get(li);
			if (i.matches("\\s+<Article .*") && first_author.equals("")) { // implicitly matches full length of string
				li++;
				while (li<lines.size()) {
					i = lines.get(li);
					if (i.matches("\\s+<ArticleTitle>.*")) {
						i=i.replaceFirst("^\\s+<ArticleTitle>", "");
						i=i.replaceFirst("</ArticleTitle>\\s*$", "");
						title = i;
					}
					if (i.matches("\\s+<Abstract>.*")) {
						while (li<lines.size()) {
							li++;
							i = lines.get(li);
							if (i.matches("\\s+</Abstract>.*")) break;
							if (i.matches("\\s+<AbstractText[^>]*>.*")) {
								i=i.replaceFirst("^\\s+<AbstractText[^>]*>", "");
								i=i.replaceFirst("</AbstractText>\\s*$", "");
								if (!abstrct.equals("")) abstrct+="\n";
								abstrct+=i;
							}
						}
						if (abstrct.equals("")) {
							bad = true;
							break;
						}
					}

					if (i.matches("\\s+<ISOAbbreviation>.*")) {
						i=i.replaceFirst("^\\s+<ISOAbbreviation>", "");
						i=i.replaceFirst("</ISOAbbreviation>\\s*$", "");
						if (!journal_abrev_title.equals("")) {
							journal_abrev_title = "";
						} else {
							journal_abrev_title = i;
						}
					}					
					if (i.matches("\\s+<PubDate>.*")) {
						li++;
						i = lines.get(li);
						if (i.matches("\\s+<Year>.*")) {
							i=i.replaceFirst("^\\s+<Year>", "");
							i=i.replaceFirst("</Year>\\s*$", "");
							pub_year = i;
						} else {
							bad = true;
							break;
						}
						li++;
						i = lines.get(li);
						if (i.matches("\\s+<Month>.*")) {
							i=i.replaceFirst("^\\s+<Month>", "");
							i=i.replaceFirst("</Month>\\s*$", "");
							pub_month = i;
						}
					}	
					if (i.matches("\\s+<Author .*")) {
						li++;
						i = lines.get(li);
						String ln = "";
						if (i.matches("\\s+<LastName>.*")) {
							i=i.replaceFirst("^\\s+<LastName>", "");
							i=i.replaceFirst("</LastName>\\s*$", "");
							ln = i;
						} else if (i.matches("\\s+<CollectiveName>.*")) {
							i=i.replaceFirst("^\\s+<CollectiveName>", "");
							i=i.replaceFirst("</CollectiveName>\\s*$", "");
							first_author = i;
						} else {
							bad = true;
							break;
						}

						if (first_author.equals("")) {
							while (li<=lines.size()) {
								li++;
								i = lines.get(li);
								if (i.matches("\\s+</Author>.*")) break;
								if (i.matches("\\s+<Initials>.*")) {
									i=i.replaceFirst("^\\s+<Initials>", "");
									i=i.replaceFirst("</Initials>\\s*$", "");
									first_author = ln +" "+ i;
								}
							}
						}
						if (!first_author.equals("")) break;
						else {
							bad = true;
							break;
						}
					}	
					li++;
				}
			}
			if (i.matches("\\s+<MedlineJournalInfo>.*") && journal_abrev_title.equals("")) {
				String jn = "";
				while (li<lines.size()) {
					li++;
					i = lines.get(li);
					if (i.matches("\\s+</MedlineJournalInfo>.*")) break;
					if (i.matches("\\s+<MedlineTA>.*")) {
						i=i.replaceFirst("^\\s+<MedlineTA>", "");
						i=i.replaceFirst("</MedlineTA>\\s*$", "");
						jn = i;
						break;
					}
				}
				if (!jn.equals("")) {
					journal_abrev_title = jn;
				}
			}
			li++;
			//if (!first_author.equals("") || bad) break;
			if (bad) break;
		}
		title = StringEscapeUtils.unescapeXml(title);
		abstrct = StringEscapeUtils.unescapeXml(abstrct);
		journal_abrev_title = StringEscapeUtils.unescapeXml(journal_abrev_title);
		first_author = StringEscapeUtils.unescapeXml(first_author);

		if (first_author.equals("") || pub_year.equals("") || /*abstrct.equals("") ||*/ title.equals("") || journal_abrev_title.equals("")) bad = true;

		if (bad) {
			System.out.println(title);
			System.out.println(first_author);
			System.out.println(abstrct);
			System.out.println(journal_abrev_title);
			System.out.println(pub_year);
			System.out.println(pub_month);
			System.out.println("xxx");
			int li2 = 0;
			while (li2<lines.size()) {
				String i = lines.get(li2);
				System.out.println(i);
				li2++;
			}
			throw new Exception();
		}

		Vector<String> toret = new Vector<>();
		toret.add(title);
		toret.add(first_author);
		toret.add(abstrct);
		toret.add(journal_abrev_title);
		toret.add(pub_year);
		toret.add(pub_month);
		return toret;		
	}				

	public static Vector<ArrayList<Integer>> DeleteCredibleSet(String gene_symbol, String storage_dir, DeleteCredibleSetPostMsg dcspm) throws Exception {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement pstatement1 = null;
		PreparedStatement pstatement2 = null;
		ResultSet rs = null;
		try {
			boolean svg_change = false;
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			rs = statement.executeQuery("select show_in_svg, marker_equivalence_set from variant_mapping_gwas_results where id = "+dcspm.gwas_db_id);
			String mes = "";
			String show = "";
			while(rs.next()) {
				mes = rs.getString("marker_equivalence_set");
				show = rs.getString("show_in_svg");
			}
			rs.close();
			String new_mes = mes;
			if (mes.equals("Credible set")) {
				if (show.equals("1")) svg_change = true;
				new_mes = "Unset";
			}
			pstatement1 = connection.prepareStatement("delete from credible_set_members where variant_mapping_gwas_result_id = ?");
			pstatement1.setInt(1, dcspm.gwas_db_id);
			pstatement1.executeUpdate();

			pstatement2 = connection.prepareStatement("update variant_mapping_gwas_results set credible_set_name = null, credible_set_posterior=null, marker_equivalence_set = ? where id = ?");
			pstatement2.setString(1, new_mes);
			pstatement2.setInt(2, dcspm.gwas_db_id);
			pstatement2.executeUpdate();
			connection.commit();
			Vector<ArrayList<Integer>> v = GetAllGWASOverlaps(connection, -1);
			ArrayList<Integer> svg_change_a = new ArrayList<>();
			if (svg_change) svg_change_a.add(Integer.valueOf(1));
			else svg_change_a.add(Integer.valueOf(0));
			v.add(svg_change_a);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			try {if (connection!=null) connection.rollback();} catch (Exception ee) {}
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement1!=null) pstatement1.close();} catch (Exception ee) {}
			try {if (pstatement2!=null) pstatement2.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}
	
	public static Vector<ArrayList<Integer>> UpdateGWASShowHide(String gene_symbol, String storage_dir, UpdateGWASShowHidePostMsg uspm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			boolean svg_change = true; // safest to default to true
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			statement = connection.createStatement();
			String show_val = "0";
			if (uspm.show_or_hide.equals("Show")) show_val = "1";
			rs = statement.executeQuery("select marker_equivalence_set from variant_mapping_gwas_results v where id = "+uspm.gwas_db_id);
			String mes = "";
			while(rs.next()) {
				mes = rs.getString("marker_equivalence_set");
			}
			rs.close();
			if (mes.equals("Unset")) svg_change = false;
			pstatement = connection.prepareStatement("update variant_mapping_gwas_results set show_in_svg = "+show_val+" where id = ?");
			pstatement.setInt(1, uspm.gwas_db_id);
			pstatement.executeUpdate();
			Vector<ArrayList<Integer>> v = GetAllGWASOverlaps(connection, -1);
			ArrayList<Integer> svg_change_a = new ArrayList<>();
			if (svg_change) svg_change_a.add(Integer.valueOf(1));
			else svg_change_a.add(Integer.valueOf(0));
			v.add(svg_change_a);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static void UpdateMarkerForLD(String gene_symbol, String storage_dir, UpdateMarkerForLDPostMsg umflpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null; 
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			String ar_val = "0";
			if (umflpm.add_or_remove.equals("Add")) ar_val = "1";
			pstatement = connection.prepareStatement("update variant_mappings set show_in_ld_groups = "+ar_val+" where id = ?");
			pstatement.setInt(1, umflpm.vm_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static void UpdateEQTLShowHide(String gene_symbol, String storage_dir, UpdateEQTLShowHidePostMsg ueshpm) throws Exception {
		Connection connection = null;
		PreparedStatement pstatement = null; 
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
			String show_val = "0";
			if (ueshpm.show_or_hide.equals("Show")) show_val = "1";
			pstatement = connection.prepareStatement("update variant_mapping_eqtl_results set show_in_svg = "+show_val+" where id = ?");
			pstatement.setInt(1, ueshpm.eqtl_db_id);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (pstatement!=null) pstatement.close();} catch (Exception ee) {}
			try {if (connection!=null) connection.close();} catch (Exception ee) {}
		}
	}

	public static Vector<ArrayList<Integer>> GetAllGWASOverlaps(Connection connection, int eqtl_id) throws Exception {
		Vector<ArrayList<Integer>> toreturn = new Vector<>();
		ArrayList<Integer> eqtl_ids = new ArrayList<>();
		ArrayList<Integer> overlap_counts = new ArrayList<>();
		
		Vector<GWASResult> agr = new Vector<>();
		PreparedStatement pstb = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			pstb = connection.prepareStatement("select v1.id as vid1, v1.name as name1, v1.start_1based as start_1based1, v1.end_1based as end_1based1 from gwas_ld_credible_set_r2_values r, variant_mappings v1 where v1.id=r.variant_mapping_id2 and r.reference_dataset_id = (select id from reference_datasets where name = ? ) and r.index_variant_mapping_id = ? and r.r2_value >= 0.6");
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			rs = statement.executeQuery("select v.name as name, v.id as vm_id, g.id as id, v.start_1based as start_1based, v.end_1based as end_1based, marker_equivalence_set, credible_set_name from variant_mapping_gwas_results g, variant_mappings v where g.index_variant_mapping_id = v.id and show_in_svg=1 and marker_equivalence_set!='Unset'");
			while(rs.next()) {
				int gwas_table_row_id = rs.getInt("id");
				String index_variant_name = rs.getString("name");
				int index_variant_mapping_id = rs.getInt("vm_id");
				int index_variant_location_start = rs.getInt("start_1based");
				int index_variant_location_end = rs.getInt("end_1based");
				String marker_equivalence_set = rs.getString("marker_equivalence_set");
				String credible_set_name = rs.getString("credible_set_name");
				if (rs.wasNull()) credible_set_name = "";
				GWASResult gwr = new GWASResult();
				gwr.index_variant_name = index_variant_name;
				gwr.index_variant_start = index_variant_location_start;
				gwr.index_variant_end = index_variant_location_end;
				gwr.gwas_table_row_id = gwas_table_row_id;
				gwr.marker_equivalence_set = marker_equivalence_set;
				gwr.credible_set_name = credible_set_name;
				agr.add(gwr);
				if (marker_equivalence_set.equals("Credible set")) {
					rs2 = statement2.executeQuery("select v1.name as name1, v1.start_1based as start_1based1, v1.end_1based as end_1based1 from credible_set_members csm, variant_mappings v1 where variant_mapping_id=v1.id and variant_mapping_gwas_result_id = "+gwas_table_row_id);						
					while (rs2.next()) {
						int s1b = rs2.getInt("start_1based1");
						int e1b = rs2.getInt("end_1based1");
						String lvn = rs2.getString("name1");
						gwr.mes_locs_starts.add(Integer.valueOf(s1b));
						gwr.mes_locs_ends.add(Integer.valueOf(e1b));
						gwr.mes_variants.add(lvn);
					}
					rs2.close();	
				} else {
					String sel_dataset = marker_equivalence_set.substring(3); // trim off leading "LD "
					pstb.setString(1,sel_dataset);
					pstb.setInt(2, index_variant_mapping_id);
					rs2 = pstb.executeQuery();					
					while (rs2.next()) {
						int s1b = rs2.getInt("start_1based1");
						int e1b = rs2.getInt("end_1based1");
						String lvn = rs2.getString("name1");
						gwr.mes_locs_starts.add(Integer.valueOf(s1b));
						gwr.mes_locs_ends.add(Integer.valueOf(e1b));
						gwr.mes_variants.add(lvn);
					}
					rs2.close();
				}
			}
			rs.close();

			if (eqtl_id==-1) {
				rs = statement.executeQuery("select v.name as name, v.id as vm_id, q.id as id, v.start_1based as start_1based, v.end_1based as end_1based, eqtl_marker_equivalence_set_id from variant_mapping_eqtl_results q, variant_mappings v where q.index_variant_mapping_id = v.id");
			} else {
				rs = statement.executeQuery("select v.name as name, v.id as vm_id, q.id as id, v.start_1based as start_1based, v.end_1based as end_1based, eqtl_marker_equivalence_set_id from variant_mapping_eqtl_results q, variant_mappings v where q.index_variant_mapping_id = v.id and q.id="+eqtl_id);	
			}
			while(rs.next()) {
				String index_variant_name = rs.getString("name");
				int index_variant_location_start = rs.getInt("start_1based");
				int index_variant_location_end = rs.getInt("end_1based");
				String eqtl_marker_equivalence_set_id = rs.getString("eqtl_marker_equivalence_set_id");
				if (rs.wasNull()) eqtl_marker_equivalence_set_id = "";
				int eqtl_table_row_id = rs.getInt("id");
				EQTLResult er = new EQTLResult();
				er.index_variant_name = index_variant_name;
				er.index_variant_start = index_variant_location_start;
				er.index_variant_end = index_variant_location_end;
				er.eqtl_marker_equivalence_set_id = eqtl_marker_equivalence_set_id;

				if (!er.eqtl_marker_equivalence_set_id.equals("")) {
					rs2 = statement2.executeQuery("select v.id as vid, v.name as name, v.start_1based as start_1based, v.end_1based as end_1based from variant_mappings v, eqtl_marker_equivalence_set_members where eqtl_marker_equivalence_set_id = "+er.eqtl_marker_equivalence_set_id+" and v.id = variant_mapping_id and r2_to_index_variant>=0.6");			
					while (rs2.next()) {
						int s1b = rs2.getInt("start_1based");
						int e1b = rs2.getInt("end_1based");
						String vn = rs2.getString("name");
						er.ld_locs_starts.add(Integer.valueOf(s1b));
						er.ld_locs_ends.add(Integer.valueOf(e1b));
						er.ld_variants.add(vn);
					}
					rs2.close();	
				} 

				int association_result_overlap_count = 0;
				for (int i=0; i<agr.size(); ++i) {
					GWASResult gw = agr.get(i);
					boolean found = false;
					if (er.index_variant_name.equals(gw.index_variant_name) && er.index_variant_start==gw.index_variant_start && er.index_variant_end==gw.index_variant_end) {
						association_result_overlap_count++;
						continue;
					}
					for (int j=0; j<gw.mes_variants.size(); ++j) {
						String var = gw.mes_variants.get(j);
						Integer vend = gw.mes_locs_ends.get(j);
						Integer vstart = gw.mes_locs_starts.get(j);
						if (er.index_variant_name.equals(var) && er.index_variant_start==vstart && er.index_variant_end==vend) {
							found = true;
							association_result_overlap_count++;
							break;
						}
					}
					if (found) continue;
					for (int k=0; k<er.ld_variants.size(); ++k) {
						String ev = er.ld_variants.get(k);
						Integer eend = er.ld_locs_ends.get(k);
						Integer estart = er.ld_locs_starts.get(k);
						if (ev.equals(gw.index_variant_name) && eend==gw.index_variant_end && estart==gw.index_variant_start) {
							association_result_overlap_count++;
							found = true;
							break;
						}
						for (int j=0; j<gw.mes_variants.size(); ++j) {
							String var = gw.mes_variants.get(j);
							Integer vend = gw.mes_locs_ends.get(j);
							Integer vstart = gw.mes_locs_starts.get(j);
							if (ev.equals(var) && eend==vend && estart==vstart) {
								found = true;
								association_result_overlap_count++;
								break;
							}
						}
						if (found) break;
					}
				}
				eqtl_ids.add(Integer.valueOf(eqtl_table_row_id));
				overlap_counts.add(Integer.valueOf(association_result_overlap_count));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception ee) {}
			try {if (rs2!=null) rs2.close();} catch (Exception ee) {}
			try {if (statement!=null) statement.close();} catch (Exception ee) {}
			try {if (statement2!=null) statement2.close();} catch (Exception ee) {}
			try {if (pstb!=null) pstb.close();} catch (Exception ee) {}
		}
		toreturn.add(eqtl_ids);
		toreturn.add(overlap_counts);
		return toreturn;
	}

	public static String GetDBInfo2(String gene_symbol, String storage_dir, int is_reset_if_1, String svg_mode, int hidenoncoding) throws Exception {
		
		File dbFile = new File(storage_dir+gene_symbol+".sqlite");
		if (!dbFile.exists()) throw new Exception();
		
		String backend_version = "1.1";
		StringBuilder sb = new StringBuilder();
		Connection connection = null;
		GeneAnnotation TargetAnnotation = new GeneAnnotation();
		Vector<GeneAnnotation> OverlappingGenes = new Vector<>();
		int chrom_low = -1;
		int chrom_high = -1;
		String chromosome = "";
		Vector<GWASResult> all_gwas_results = new Vector<>();
		Vector<EQTLResult> all_eqtl_results = new Vector<>();
		String resource_name = "";
		String target_name = "";
		Integer tss = -1;
		Integer tss_dir = 0;
		Vector<CodingVariantGeneAllele> cvga = new Vector<>();
		Vector<String> common_coding_populations = new Vector<>();
		Vector<String> gwas_eqtl_index_variants = new Vector<>();
		Vector<String> all_ld_datasets = new Vector<>();
		Vector<String> all_ld_dataset_ids = new Vector<>();
		Vector<SourceDocument> allsourcedocuments = new Vector<>();		
		Hashtable<Integer, String> ld_datasets_by_id = new Hashtable<>();
		Hashtable<String, IndexVariant> assoc_result_hash = new Hashtable<>();
		connection = DriverManager.getConnection("jdbc:sqlite:"+storage_dir+gene_symbol+".sqlite");
		Statement statement = connection.createStatement();
		Statement statement2 = connection.createStatement();
		Statement statement3 = connection.createStatement();
		
		/*
		 * DB TWEAKS
		 */
		
		if (1==1) {
			DatabaseMetaData md = connection.getMetaData();
			
			boolean add_pqtl = true;
			ResultSet rs = md.getColumns(null, null, "variant_mapping_gwas_results", "is_pqtl");
			while (rs.next()) {
				add_pqtl = false;
			}
			rs.close();
			
			if (add_pqtl) {
				statement.executeUpdate("alter table variant_mapping_gwas_results add is_pqtl INTEGER NOT NULL DEFAULT 0");
				statement.executeUpdate("update variant_mapping_gwas_results set is_pqtl=1 where curator_comment like 'pQTL from INTERVAL%'");
			}
			
			
			
		}
		
		
		if (1==0) {
			
			//statement.executeUpdate("CREATE TABLE if not exists gene_phenotype_associations (gene_id TEXT NOT NULL, gene_symbol TEXT NOT NULL, phenotype TEXT NOT NULL, source TEXT NOT NULL, source_id TEXT NOT NULL, source_linkout TEXT, meta_resource_id INTEGER NOT NULL REFERENCES meta_resources(id), when_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
			
			statement.executeUpdate("CREATE TABLE if not exists allele_phenotype_associations (variant_mapping_id INTEGER NOT NULL REFERENCES variant_mappings(id), phenotype TEXT NOT NULL, source TEXT NOT NULL, source_id TEXT NOT NULL, source_linkout TEXT, unaudited_risk_allele TEXT, clinical_significance TEXT, meta_resource_id INTEGER NOT NULL REFERENCES meta_resources(id), when_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
			
			DatabaseMetaData md = connection.getMetaData();
			boolean new_r2_table_found = false;
			ResultSet rs = md.getTables(null, null, "gwas_ld_credible_set_r2_values", null);
			while (rs.next()) {
				new_r2_table_found = true;
			}
			rs.close();
			
			boolean add_effect_allele = true;
			rs = md.getColumns(null, null, "variant_mapping_eqtl_results", "effect_allele");
			while (rs.next()) {
				add_effect_allele = false;
			}
			rs.close();
			
			if (add_effect_allele) {
				statement.executeUpdate("alter table variant_mapping_eqtl_results add effect_allele TEXT");
			}
			
			boolean add_comment = true;
			rs = md.getColumns(null, null, "source_documents", "curator_comment");
			while (rs.next()) {
				add_comment = false;
			}
			rs.close();
			
			if (add_comment) {
				statement.executeUpdate("alter table source_documents add curator_comment TEXT");
				statement.executeUpdate("alter table source_documents add has_been_reviewed INTEGER");
				statement.executeUpdate("update source_documents set has_been_reviewed=0");
			}
			
			if (!new_r2_table_found) {
				
				statement.executeUpdate("CREATE TABLE gwas_ld_credible_set_r2_values (index_variant_mapping_id INTEGER NOT NULL REFERENCES variant_mappings(id), variant_mapping_id2 INTEGER NOT NULL REFERENCES variant_mappings(id), r2_value NUMBER NOT NULL, reference_dataset_id INTEGER NOT NULL REFERENCES reference_datasets(id), meta_resource_id INTEGER NOT NULL REFERENCES meta_resources(id), when_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

				statement.executeUpdate("CREATE INDEX grv_index1 on gwas_ld_credible_set_r2_values(index_variant_mapping_id)");
				statement.executeUpdate("CREATE INDEX grv_index2 on gwas_ld_credible_set_r2_values(variant_mapping_id2)");
				statement.executeUpdate("CREATE INDEX grv_index3 on gwas_ld_credible_set_r2_values(reference_dataset_id)");
				statement.executeUpdate("CREATE INDEX grv_index4 on gwas_ld_credible_set_r2_values(r2_value)");
				
				statement.executeUpdate("CREATE INDEX vmaaemf_index2 on variant_mapping_alt_allele_exacsubpop_max_frequencies (frequency)");
				
				Vector<String> ivs = new Vector<>();
				rs = statement.executeQuery("select distinct index_variant_mapping_id from variant_mapping_gwas_results");
				while (rs.next()) {
					String iv = rs.getString(1);
					ivs.add(iv);
				}
				rs.close();
				
				String isql = "insert into gwas_ld_credible_set_r2_values (" +
						"index_variant_mapping_id,"+
						"variant_mapping_id2,"+
						"r2_value,"+
						"reference_dataset_id,"+
						"meta_resource_id, "+
						"when_created ) select variant_mapping_id1, variant_mapping_id2, r2_value, reference_dataset_id, meta_resource_id, when_created from r2_values where variant_mapping_id1 = ?";
				PreparedStatement pstmt = connection.prepareStatement(isql);
				String isql2 = "insert into gwas_ld_credible_set_r2_values (" +
						"index_variant_mapping_id,"+
						"variant_mapping_id2,"+
						"r2_value,"+
						"reference_dataset_id,"+
						"meta_resource_id, "+
						"when_created ) select variant_mapping_id2, variant_mapping_id1, r2_value, reference_dataset_id, meta_resource_id, when_created from r2_values where variant_mapping_id2 = ?";
				PreparedStatement pstmt2 = connection.prepareStatement(isql2);
				
				
				for (int i=0; i<ivs.size(); ++i) {
					String ivid = ivs.get(i);
					System.out.println("migrating "+ivid);
					pstmt.setString(1, ivid);
					pstmt.executeUpdate();
					pstmt2.setString(1, ivid);
					pstmt2.executeUpdate();
				}
				pstmt.close();
				pstmt2.close();
				
			}
		}
		
		
		/*
		 * 
		 */
		
		String db_version = "";
		ResultSet rs = statement.executeQuery("select value from other_info where parameter = 'db_version'");
		while(rs.next()) {
			db_version = rs.getString(1);
		}
		rs.close();
		
		BitSet ldgmarkersbs = new BitSet();
		Vector<Integer> ldglookup = new Vector<>();
		Vector<LDGroup> ldgroups = new Vector<>();
		Hashtable<Integer, LDGMarker> ldgmarkers = new Hashtable<>();

		boolean output_profile = false;
		long pmillis = -1;
		long delta = 0;
		long cmillis = System.currentTimeMillis();
		if (output_profile) System.out.println("starting");
		pmillis = cmillis;

		// recover gene annotations and other basics
		
		rs = statement.executeQuery("select id, symbol, gene_id, transcript_id, exon_starts_1based, exon_ends_1based, coding_start_1based, coding_end_1based, is_forward_strand, is_gene_target from transcript_annotations");
		while(rs.next()) {
			GeneAnnotation ga = new GeneAnnotation();
			ga.gene_id = rs.getString("gene_id");
			ga.db_id = rs.getString("id");
			ga.is_gene_target = rs.getInt("is_gene_target");
			ga.is_forward_strand = rs.getInt("is_forward_strand");
			ga.transcript_id = rs.getString("transcript_id");
			ga.symbol = rs.getString("symbol");
			ga.coding_start = rs.getInt("coding_start_1based");
			if (rs.wasNull()) ga.coding_start = -1;
			ga.coding_end = rs.getInt("coding_end_1based");
			if (rs.wasNull()) ga.coding_end = -1;
			if (ga.coding_start==-1 && hidenoncoding==1 && ga.is_gene_target!=1) continue;
			String es = rs.getString("exon_starts_1based");
			String ee = rs.getString("exon_ends_1based");
			String[] esa = es.split(" ");
			String[] eea = ee.split(" ");
			ga.num_exons = esa.length;
			for (int i=0; i<ga.num_exons; ++i) {
				ga.exon_starts.add(Integer.valueOf(esa[i]));
				ga.exon_ends.add(Integer.valueOf(eea[i]));
			}
			if (ga.is_gene_target==1) {
				TargetAnnotation = ga;
				target_name = ga.symbol;
				int lowest = ga.exon_starts.get(0);
				int highest = ga.exon_ends.get(ga.exon_ends.size()-1);
				if (lowest>highest) {
					lowest = ga.exon_starts.get(ga.exon_starts.size()-1);
					highest = ga.exon_ends.get(0);
				}
				if (ga.is_forward_strand==1) {
					tss = lowest;
					tss_dir = 1;
				} else {
					tss = highest;
					tss_dir = -1;
				}
			} else {
				OverlappingGenes.add(ga);
			}
		}
		rs.close();
		rs = statement.executeQuery("select low_1based, high_1based, chromosome from reference_genomes");
		while(rs.next()) {
			chrom_low = rs.getInt("low_1based");
			chrom_high = rs.getInt("high_1based");
			chromosome = rs.getString("chromosome");
		}
		rs.close();

		String summary = "";
		rs = statement.executeQuery("select summary from project_summary");
		while(rs.next()) {
			summary = rs.getString("summary");
			if (rs.wasNull()) summary="";
		}
		rs.close();

		rs = statement.executeQuery("select name from meta_resources where name like 'Ensembl%'");
		while(rs.next()) {
			resource_name = rs.getString("name");
		}
		rs.close();

		rs = statement.executeQuery("select id, name from reference_datasets where is_ld_reference_dataset=1 order by name");
		while(rs.next()) {
			String nm = rs.getString("name");
			int id = rs.getInt("id");
			all_ld_datasets.add(nm);
			all_ld_dataset_ids.add(String.valueOf(id));
			ld_datasets_by_id.put(Integer.valueOf(id), nm);
		}
		rs.close();

		rs = statement.executeQuery("select name from reference_datasets where is_ccv_reference_dataset = 1 order by name");
		while(rs.next()) {
			String nm = rs.getString("name");
			nm = nm.replace("ExAC:", "");
			common_coding_populations.add(nm);
		}
		rs.close();

		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done collecting basics "+delta+" ms");
		pmillis = cmillis;

		Vector<Integer> vma_ids = new Vector<>();	 
		Vector<Integer> vm_ids = new Vector<>();	
		Vector<Double> max_fs = new Vector<>();	

		/*
			 collect all variant-mapping alt-alleles with frequency >=.001 and is_coding_impact=1 within the view port
			 (.001 applies only to major 1KG ref populations)
		 */
		
		String ord = "asc";
		if (TargetAnnotation.is_forward_strand!=1) {ord = "desc";}
		rs = statement.executeQuery("select a.id as id, m.id as vmid, x.frequency as mfreq from variant_mappings m, variant_mapping_alleles a, variant_mapping_alt_allele_exacsubpop_max_frequencies x, variant_mapping_alternate_allele_impacts v where v.is_coding_impact=1 and v.variant_mapping_allele_id=a.id and m.id = a.variant_mapping_id and a.id=x.variant_mapping_allele_id and x.frequency>=.001 and is_reference_allele=0 and end_1based >="+chrom_low+" and start_1based <= "+chrom_high+" order by start_1based "+ord);
		while(rs.next()) {
			Integer id = Integer.valueOf(rs.getInt("id"));
			Integer vmid = Integer.valueOf(rs.getInt("vmid"));
			Double mf = rs.getDouble("mfreq");
			if (!vma_ids.contains(id)) {
				vma_ids.add(id);
				vm_ids.add(vmid);
				max_fs.add(mf);
			}
		}
		rs.close();

		/*
			 For each above collected allele, collect the coding impact against each gene
			 I think it is the case that there will always only be a single impact for any marker/allele/gene combination (i.e. coding change and splice impact won't be annotated separately (even though perhaps they should be)), but we'll collect allowing for that not to be the case.
		 */
		
		PreparedStatement pstatementccv1 = connection.prepareStatement("select i.id as id, m.name as name, m.show_in_ld_groups as show, t.symbol as symbol, start_1based, allele, sift, polyphen, so_terms, display_terms, ranks, impact_terms, codon_allele_string, hgvs_protein, hgvs_genomic from variant_mapping_alternate_allele_impacts i, variant_mapping_alleles a, variant_mappings m, transcript_annotations t where i.variant_mapping_allele_id=? and a.id=? and a.variant_mapping_id = m.id and i.transcript_annotation_id = t.id and i.is_coding_impact=1");
		PreparedStatement pstatementccv2 = connection.prepareStatement("select name, frequency, is_ccv_reference_dataset from variant_mapping_allele_frequencies a, reference_datasets d where a.variant_mapping_allele_id = ? and d.id=a.reference_dataset_id");
		for (int i=0; i<vma_ids.size(); ++i) {
			int vma_id = vma_ids.get(i);
			Integer vmid = vm_ids.get(i);
			Double mf = max_fs.get(i);
			pstatementccv1.setInt(1, vma_id);
			pstatementccv1.setInt(2, vma_id);
			rs = pstatementccv1.executeQuery();
			while(rs.next()) {
				CodingVariantGeneAllele CV = new CodingVariantGeneAllele();
				String show_in_ld_groups = rs.getString("show");
				CV.gene_symbol = rs.getString("symbol");
				CV.db_id = rs.getString("id");
				CV.max_gnomadg_subpop_freq = mf;
				BigDecimal bd1 = new BigDecimal(mf);
				bd1 = bd1.setScale(3, RoundingMode.HALF_UP);
				CV.max_gnomadg_subpop_freq_str = bd1.toString();
				CV.name = rs.getString("name");
				CV.vma_id = vma_id;
				CV.vm_id = vmid;
				CV.start_1based = rs.getInt("start_1based");
				CV.allele = rs.getString("allele");
				CV.sift = rs.getString("sift"); if (rs.wasNull()) CV.sift="";
				CV.polyphen = rs.getString("polyphen"); if (rs.wasNull()) CV.polyphen="";
				CV.so_terms = rs.getString("so_terms"); if (rs.wasNull()) CV.so_terms="";
				CV.display_terms = rs.getString("display_terms"); if (rs.wasNull()) CV.display_terms="";
				CV.ranks = rs.getString("ranks"); if (rs.wasNull()) CV.ranks="";
				CV.impact_terms = rs.getString("impact_terms"); if (rs.wasNull()) CV.impact_terms="";
				CV.codon_allele_string = rs.getString("codon_allele_string"); if (rs.wasNull()) CV.codon_allele_string="";
				CV.hgvs_protein = rs.getString("hgvs_protein"); if (rs.wasNull()) CV.hgvs_protein="";
				CV.hgvs_genomic = rs.getString("hgvs_genomic"); if (rs.wasNull()) CV.hgvs_genomic="";
				pstatementccv2.setInt(1, vma_id);
				ResultSet rs2 = pstatementccv2.executeQuery();
				while(rs2.next()) {
					String is_ccv = rs2.getString("is_ccv_reference_dataset");
					String nm = rs2.getString("name");
					if (nm.equals("gnomADg:ALL")) {
						 double fr = rs2.getDouble("frequency");
						 bd1 = new BigDecimal(fr);
						 bd1 = bd1.setScale(3, RoundingMode.HALF_UP);
						 CV.gnomadgall_freq_str = bd1.toString();
					} else if (is_ccv.equals("1")) {
					 nm = nm.replace("gnomADg:", "");
					 double fr = rs2.getDouble("frequency");
					 bd1 = new BigDecimal(fr);
					 bd1 = bd1.setScale(3, RoundingMode.HALF_UP);
					 CV.frequencies.put(nm, bd1.toString());
					}
				}
				rs2.close();
				if (show_in_ld_groups.equals("1")) {
					CV.show_in_ld_groups = true;
					LDGMarker lm = null;
					if (ldgmarkers.containsKey(vmid)) {
						lm = ldgmarkers.get(vmid);
					} else {
						lm = new LDGMarker();
						lm.id = vmid;
						lm.name = CV.name;
						lm.start_1based = CV.start_1based;
						ldgmarkers.put(vmid, lm);
						int idx = ldglookup.indexOf(vmid);
						if (idx==-1) {
							idx = ldglookup.size();
							ldglookup.addElement(vmid);
						}
						ldgmarkersbs.set(idx);
					}
					lm.coding_impacts.add(CV);
				}
				cvga.add(CV);
			}
			rs.close();
		}
		pstatementccv1.close();
		pstatementccv2.close();
		
		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done collecting common coding variants "+delta+" ms");
		pmillis = cmillis;

		// source documents

		
		int unreviewed_reference_count = 0;
		int total_reference_count = 0;
		
		rs = statement.executeQuery("select source_documents.id as id, document_type, document_year, document_type_db_location, outbound_link, name, curator_comment, has_been_reviewed from source_documents, meta_resources where meta_resources.id = meta_resource_id order by document_year desc");
		while (rs.next()) {
			String id = rs.getString("id");
			String type = rs.getString("document_type");
			String type_db_location = rs.getString("document_type_db_location");
			String linkout = rs.getString("outbound_link");
			if (rs.wasNull()) linkout = "";
			String resource = rs.getString("name");
			String document_year = rs.getString("document_year");
			int hbr = rs.getInt("has_been_reviewed");
			
			total_reference_count++;
			if (hbr==0) unreviewed_reference_count++;
			
			String cc = rs.getString("curator_comment");
			if (rs.wasNull()) cc = "";

			SourceDocument sd = new SourceDocument();
			sd.id = id;
			sd.outbound_link = linkout;
			sd.has_been_reviewed = hbr;
			sd.curator_comment = cc;
			if (resource.indexOf("Ensembl")>-1) sd.automatic = true;
			else sd.automatic = false;
			if (type.equals("pubmed")) {
				String p_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
				ResultSet rs2 = statement2.executeQuery("select id, title, pubmed_id, first_author, journal, pub_year from pubmed_documents where id = "+p_id);
				while (rs2.next()) {
					String pubmed_document_id = rs2.getString("id");
					String title = rs2.getString("title");
					String pubmed_id = rs2.getString("pubmed_id");
					String first_author = rs2.getString("first_author");
					String journal = rs2.getString("journal");
					String pub_year = rs2.getString("pub_year");
					PubmedDocument pd = new PubmedDocument();
					pd.first_author = first_author;
					pd.title = title;
					pd.journal = journal;
					pd.pubmed_id = pubmed_id;
					pd.id = pubmed_document_id;
					pd.pub_year = pub_year;
					sd.pmdoc = pd;
					ResultSet rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
					while (rs3.next()) {
						Detail d = new Detail();
						d.id = rs3.getString("id");
						d.b64_contents = rs3.getString("detail_contents");
						d.section_assignment = rs3.getString("section_assignment");
						if (rs3.wasNull()) d.section_assignment = "";
						d.description = rs3.getString("description");
						sd.details.add(d);
					}
					rs3.close();
				}
				rs2.close();
			} else if (type.equals("biorxiv")) {
				String b_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
				ResultSet rs2 = statement2.executeQuery("select id, title, doi_id, first_author, publisher, pub_year, pub_date from biorxiv_documents where id = "+b_id);
				while (rs2.next()) {
					String biorxiv_document_id = rs2.getString("id");
					String title = rs2.getString("title");
					String doi_id = rs2.getString("doi_id");
					String first_author = rs2.getString("first_author");
					String publisher = rs2.getString("publisher");
					String pub_year = rs2.getString("pub_year");
					String pub_date = rs2.getString("pub_date");						
					BiorxivDocument bd = new BiorxivDocument();
					bd.first_author = first_author;
					bd.title = title;
					bd.publisher = publisher;
					bd.doi_id = doi_id;
					bd.id = biorxiv_document_id;
					bd.pub_year = pub_year;
					bd.pub_date = pub_date;
					sd.bdoc = bd;
					ResultSet rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
					while (rs3.next()) {
						Detail d = new Detail();
						d.id = rs3.getString("id");
						d.b64_contents = rs3.getString("detail_contents");
						d.section_assignment = rs3.getString("section_assignment");
						if (rs3.wasNull()) d.section_assignment = "";
						d.description = rs3.getString("description");
						sd.details.add(d);
					}
					rs3.close();
				}
				rs2.close();
			} else if (type.equals("file")) {
				String f_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
				ResultSet rs2 = statement2.executeQuery("select id, name, description, contents from file_documents where id = "+f_id);
				while (rs2.next()) {
					String file_document_id = rs2.getString("id");
					String name = rs2.getString("name");
					String description = rs2.getString("description");
					String contents = rs2.getString("contents");
					FileDocument fd = new FileDocument();
					fd.contents = contents;
					fd.name = name;
					fd.id = file_document_id;
					fd.description = description;
					fd.year = document_year;
					sd.fdoc = fd;
					ResultSet rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
					while (rs3.next()) {
						Detail d = new Detail();
						d.id = rs3.getString("id");
						d.b64_contents = rs3.getString("detail_contents");
						d.section_assignment = rs3.getString("section_assignment");
						if (rs3.wasNull()) d.section_assignment = "";
						d.description = rs3.getString("description");
						sd.details.add(d);
					}
					rs3.close();
				}
				rs2.close();
			} else if (type.equals("web")) {
				String w_id = type_db_location.substring(type_db_location.indexOf(" ")+1);
				ResultSet rs2 = statement2.executeQuery("select id, site, description from web_documents where id = "+w_id);
				while (rs2.next()) {
					String web_document_id = rs2.getString("id");
					String site = rs2.getString("site");
					String description = rs2.getString("description");
					WebDocument wd = new WebDocument();
					wd.site = site;
					wd.id = web_document_id;
					wd.description = description;
					wd.year = document_year;
					sd.wdoc = wd;
					ResultSet rs3 = statement3.executeQuery("select id, section_assignment, description, detail_contents from source_document_details where source_document_id = "+id+" order by id");
					while (rs3.next()) {
						Detail d = new Detail();
						d.id = rs3.getString("id");
						d.b64_contents = rs3.getString("detail_contents");
						d.section_assignment = rs3.getString("section_assignment");
						if (rs3.wasNull()) d.section_assignment = "";
						d.description = rs3.getString("description");
						sd.details.add(d);
					}
					rs3.close();
				}
				rs2.close();
			}
			allsourcedocuments.add(sd);
		}
		rs.close();

		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done collecting source documents "+delta+" ms");
		pmillis = cmillis;

		// GWAS results

		PreparedStatement pstm = connection.prepareStatement("select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = ?) and is_coding_impact=1");		
		PreparedStatement pst = connection.prepareStatement("select v1.id as vid1, v1.name as name1, v1.start_1based as start_1based1, v1.end_1based as end_1based1, (select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = v1.id) and is_coding_impact=1) as v1chk, v2.id as vid2, v2.name as name2, v2.start_1based as start_1based2, v2.end_1based as end_1based2, (select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = v2.id) and is_coding_impact=1) as v2chk,r.r2_value as r2_value from r2_values r inner join variant_mappings v1 on  v1.id=r.variant_mapping_id1 inner join variant_mappings v2 on v2.id=r.variant_mapping_id2 where r.reference_dataset_id = (select id from reference_datasets where name = ? ) and (r.variant_mapping_id1 = ? or r.variant_mapping_id2 = ?) and r.r2_value >= 0.6");
		PreparedStatement pstb = connection.prepareStatement("select v1.id as vid1, v1.name as name1, v1.start_1based as start_1based1, v1.end_1based as end_1based1, (select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = v1.id) and is_coding_impact=1) as v1chk,r.r2_value as r2_value from gwas_ld_credible_set_r2_values r, variant_mappings v1 where v1.id=r.variant_mapping_id2 and r.reference_dataset_id = (select id from reference_datasets where name = ? ) and r.index_variant_mapping_id = ? and r.r2_value >= 0.6 order by v1.start_1based");
		Hashtable<String, String> is_coding_hash = new Hashtable<>();
		rs = statement.executeQuery("select v.name as name, m.name as mname, svg_display_name, v.id as vm_id, g.id as id, v.start_1based as start_1based, v.end_1based as end_1based, trait, column_display_text, outbound_link, document_year, pvalue, or_beta, allele, allele_is_fstrand_audited, g.curator_comment as curator_comment, show_in_svg, marker_equivalence_set, credible_set_name, credible_set_posterior, is_pqtl from variant_mapping_gwas_results g, variant_mappings v, source_documents p, meta_resources m where g.index_variant_mapping_id = v.id and p.id=source_document_id and m.id=g.meta_resource_id order by document_year desc");
		while(rs.next()) {
			long g_cmillis = System.currentTimeMillis();
			long g_pmillis = g_cmillis;
			int gwas_table_row_id = rs.getInt("id");
			String svg_display_name = rs.getString("svg_display_name");
			String index_variant_name = rs.getString("name");
			int index_variant_mapping_id = rs.getInt("vm_id");
			int index_variant_location_start = rs.getInt("start_1based");
			int index_variant_location_end = rs.getInt("end_1based");
			boolean is_coding = false;
			ResultSet rs2 = statement2.executeQuery("select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+index_variant_mapping_id+") and is_coding_impact=1");
			while (rs2.next()) {
				int chk = rs2.getInt(1);
				if (chk>0) is_coding = true;
			}
			rs2.close();
			Integer is_pqtl = rs.getInt("is_pqtl");
			String trait = rs.getString("trait");
			String column_display_text = rs.getString("column_display_text");
			String outbound_link = rs.getString("outbound_link");
			String pvalue = String.format("%.2e", Double.valueOf(rs.getString("pvalue")));
			String or_beta = rs.getString("or_beta");
			if (rs.wasNull()) or_beta = "";
			int document_year = rs.getInt("document_year");
			String myor = or_beta;
			try {
				BigDecimal bd1 = new BigDecimal(myor);
				bd1 = bd1.setScale(2, RoundingMode.HALF_UP);
				myor = bd1.toString();
			} catch (Exception eee) {
				myor = or_beta;
			}
			or_beta = myor;
			String allele = rs.getString("allele");
			if (rs.wasNull()) allele = "";
			String allele_sup = "";
			String f_audited = rs.getString("allele_is_fstrand_audited");
			if (f_audited.equals("1")) allele_sup ="<br>Audited";
			//String allele_matches_fstrand_variant_allele = rs.getString("allele_matches_fstrand_variant_allele");
			//if (allele_matches_fstrand_variant_allele.equals("0") && !allele.equals("")) allele+="*";
			String curator_comment = rs.getString("curator_comment"); if (rs.wasNull()) curator_comment = "";
			String marker_equivalence_set = rs.getString("marker_equivalence_set");
			String credible_set_name = rs.getString("credible_set_name");
			if (rs.wasNull()) credible_set_name = "";
			double credible_set_posterior = -1.0;
			String csp = rs.getString("credible_set_posterior");
			if (!rs.wasNull() && !csp.equals("")) {
				credible_set_posterior = Double.valueOf(csp);
			}
			String show_in_svg = rs.getString("show_in_svg");
			IndexVariant giv;
			String vkey = index_variant_name+" "+index_variant_location_start+" "+index_variant_location_end;
			if (!gwas_eqtl_index_variants.contains(vkey)) {
				gwas_eqtl_index_variants.add(vkey);
				giv = new IndexVariant();
				giv.index_variant_name = index_variant_name;
				giv.index_variant_location_start = index_variant_location_start;
				giv.index_variant_location_end = index_variant_location_end;
				giv.is_coding = is_coding;
				assoc_result_hash.put(vkey, giv);
			} else {
				giv = assoc_result_hash.get(vkey);
			}
			GWASResult gwr = new GWASResult();
			String nm = rs.getString("mname");
			if (nm.indexOf("Ensembl")>-1) gwr.automatic = true;
			if (is_pqtl==1) gwr.is_pqtl = true;
			gwr.allele = allele;
			gwr.allele_sup = allele_sup;
			gwr.svg_display_name = svg_display_name;
			gwr.index_variant_name = index_variant_name;
			gwr.index_variant_start = index_variant_location_start;
			gwr.index_variant_end = index_variant_location_end;
			gwr.column_display_text = column_display_text;
			gwr.outbound_link = outbound_link;
			gwr.document_year = document_year;
			gwr.or_beta = or_beta;
			gwr.pvalue = pvalue;
			gwr.trait = trait;
			gwr.gwas_table_row_id = gwas_table_row_id;
			gwr.curator_comment = curator_comment;
			if (show_in_svg.equals("1")) gwr.show_in_svg = true;
			gwr.marker_equivalence_set = marker_equivalence_set;
			gwr.credible_set_name = credible_set_name;
			gwr.credible_set_posterior = credible_set_posterior;
			giv.gwas_results.add(gwr);
			all_gwas_results.add(gwr);
			if (gwr.show_in_svg && !gwr.marker_equivalence_set.equals("Unset")) {
				LDGMarker lm = null;
				if (ldgmarkers.containsKey(Integer.valueOf(index_variant_mapping_id))) {
					lm = ldgmarkers.get(Integer.valueOf(index_variant_mapping_id));
				} else {
					lm = new LDGMarker();
					lm.id = index_variant_mapping_id;
					lm.name = index_variant_name;
					lm.start_1based = index_variant_location_start;
					ldgmarkers.put(Integer.valueOf(index_variant_mapping_id), lm);
					Integer ii = Integer.valueOf(index_variant_mapping_id);
					int idx = ldglookup.indexOf(ii);
					if (idx==-1) {
						idx = ldglookup.size();
						ldglookup.addElement(ii);
					}
					ldgmarkersbs.set(idx);
				}
				lm.gwas_results.add(gwr);
			}

			g_cmillis = System.currentTimeMillis();
			long g_delta = g_cmillis - g_pmillis;		
			if (output_profile) System.out.println(" stage 1: "+g_delta+" ms");
			g_pmillis = g_cmillis;

			rs2 = statement2.executeQuery("select max(frequency) from variant_mapping_allele_frequencies where reference_dataset_id = (select id from reference_datasets where name = '1000GENOMES:phase_3:EUR') and variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+index_variant_mapping_id+")");
			while (rs2.next()) {
				Double mf = rs2.getDouble(1);
				BigDecimal bd1 = new BigDecimal(1.0-mf);
				bd1 = bd1.setScale(3, RoundingMode.HALF_UP);
				gwr.sum_1kgp3eur_maf_str = bd1.toString();
			}
			rs2.close();
			
			Vector<String> typed_in = new Vector<>();
			rs2 = statement2.executeQuery("select distinct d.name as name from reference_datasets d, variant_mapping_allele_frequencies f where f.variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+index_variant_mapping_id+") and d.id=f.reference_dataset_id");
			while (rs2.next()) {
				String mnm = rs2.getString("name");
				typed_in.add(mnm);
			}
			rs2.close();

			{
				PreparedStatement pstatement = connection.prepareStatement("select reference_dataset_id, count(reference_dataset_id) as ct from r2_values where r2_value>=0.6 and (variant_mapping_id1 = ? or variant_mapping_id2 = ?) group by reference_dataset_id");

				pstatement.setInt(1, index_variant_mapping_id);
				pstatement.setInt(2, index_variant_mapping_id);
				Hashtable<String,String> cts = new Hashtable<>();
				rs2 = pstatement.executeQuery();
				while (rs2.next()) {
					String id = rs2.getString(1);
					String count = rs2.getString(2);
					cts.put(id, count);
				}
				rs2.close();

				for (int i=0; i<all_ld_datasets.size(); ++i) {
					String ds = all_ld_datasets.get(i);
					String dsid = all_ld_dataset_ids.get(i);
					if (typed_in.contains(ds)) {
						if (cts.containsKey(dsid)) {
							gwr.LDdatasets.add("LD "+ds);
							gwr.LDdatasetsHighR2Count.add(cts.get(dsid));	
						} else {
							gwr.LDdatasets.add("LD "+ds);
							gwr.LDdatasetsHighR2Count.add("0");
						}
					} else {
						gwr.LDdatasets.add("LD "+ds);
						gwr.LDdatasetsHighR2Count.add("n/a");
					}
				}
				pstatement.close();
			}

			g_cmillis = System.currentTimeMillis();
			g_delta = g_cmillis - g_pmillis;		
			if (output_profile) System.out.println(" stage 2: "+g_delta+" ms");
			g_pmillis = g_cmillis;

			if (marker_equivalence_set.equals("Credible set")) {
				gwr.typed_in_accepted_dataset = true; // arbitrary
				
				rs2 = statement2.executeQuery("select v1.id as vid1, v1.name as name1, v1.start_1based as start_1based1, v1.end_1based as end_1based1, posterior from credible_set_members csm, variant_mappings v1 where variant_mapping_id=v1.id and variant_mapping_gwas_result_id = "+gwas_table_row_id);						
				
				while (rs2.next()) {
					int vid1 = rs2.getInt("vid1");
					String pos = rs2.getString("posterior");
					boolean v_is_coding = false;
					int s1b = rs2.getInt("start_1based1");
					int e1b = rs2.getInt("end_1based1");
					String lvn = rs2.getString("name1");
					if (is_coding_hash.containsKey(String.valueOf(vid1))) {
						String v = is_coding_hash.get(String.valueOf(vid1));
						if (v.equals("1")) v_is_coding = true;
					} else {
						pstm.setInt(1, vid1);
						ResultSet rs3 = pstm.executeQuery();
						while (rs3.next()) {
							int chk = rs3.getInt(1);
							if (chk>0) v_is_coding = true;
						}
						rs3.close();
						if (v_is_coding) is_coding_hash.put(String.valueOf(vid1), "1");
						else is_coding_hash.put(String.valueOf(vid1), "0");
					}
					int insert_before_this_index=-1;
					for (int ij=0; ij<gwr.mes_locs_starts.size(); ++ij) {
						Integer lls = gwr.mes_locs_starts.get(ij);
						if (lls>s1b) {
							insert_before_this_index=ij;
							break;
						}
					}
					if (insert_before_this_index==-1) {
						gwr.mes_locs_starts.add(Integer.valueOf(s1b));
						gwr.mes_locs_ends.add(Integer.valueOf(e1b));
						gwr.mes_variants.add(lvn);
						gwr.mes_posterior.add(pos);
						if (v_is_coding) gwr.mes_coding.add("1");
						else gwr.mes_coding.add("0");
					} else {
						gwr.mes_locs_starts.add(insert_before_this_index, Integer.valueOf(s1b));
						gwr.mes_locs_ends.add(insert_before_this_index, Integer.valueOf(e1b));
						gwr.mes_variants.add(insert_before_this_index, lvn);	
						gwr.mes_posterior.add(insert_before_this_index, pos);
						if (v_is_coding) gwr.mes_coding.add(insert_before_this_index, "1");
						else gwr.mes_coding.add(insert_before_this_index, "0");
					} 
				}
				rs2.close();	
				gwr.credible_set_member_count = gwr.mes_posterior.size();
			}
			
			if (!marker_equivalence_set.equals("None") && !marker_equivalence_set.equals("Unset") && !marker_equivalence_set.equals("Credible set")) {
				String sel_dataset = marker_equivalence_set.substring(3); // trim off leading "LD "

				gwr.typed_in_accepted_dataset = false;
				int idx = gwr.LDdatasets.indexOf(marker_equivalence_set);
				if (idx>-1) {
					String c = gwr.LDdatasetsHighR2Count.get(idx);
					if (!c.equals("n/a")) gwr.typed_in_accepted_dataset = true;
				}
				rs2 = statement2.executeQuery("select count(*) from credible_set_members where variant_mapping_gwas_result_id = "+gwr.gwas_table_row_id);
				while (rs2.next()) {
					gwr.credible_set_member_count = rs2.getInt(1);
				}
				rs2.close();
				pstb.setString(1,sel_dataset);
				pstb.setInt(2, index_variant_mapping_id);
				rs2 = pstb.executeQuery();					
				while (rs2.next()) {
					String r2 = rs2.getString("r2_value");
					boolean v_is_coding = false;
					int s1b = rs2.getInt("start_1based1");
					int e1b = rs2.getInt("end_1based1");
					String lvn = rs2.getString("name1");
					int v1chk = rs2.getInt("v1chk");
					if (v1chk>0) v_is_coding = true;
					gwr.mes_locs_starts.add(Integer.valueOf(s1b));
					gwr.mes_locs_ends.add(Integer.valueOf(e1b));
					gwr.mes_variants.add(lvn);
					gwr.mes_r2.add(r2);
					if (v_is_coding) gwr.mes_coding.add("1");
					else gwr.mes_coding.add("0");

				}
				rs2.close();
			}

			g_cmillis = System.currentTimeMillis();
			g_delta = g_cmillis - g_pmillis;		
			if (output_profile) System.out.println(" stage 3: "+g_delta+" ms");
			g_pmillis = g_cmillis;

		}
		rs.close();
		pstm.close();
		pst.close();
		pstb.close();
		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done collecting gwas "+delta+" ms");
		pmillis = cmillis;

		//// EQTL results

		rs = statement.executeQuery("select v.name as name, svg_display_name, v.id as vm_id, q.id as id, v.start_1based as start_1based, v.end_1based as end_1based, gene_symbol, effect_allele, tissue, column_display_text, outbound_link, document_year, pvalue, beta, show_in_svg, q.curator_comment as curator_comment, eqtl_marker_equivalence_set_id from variant_mapping_eqtl_results q, variant_mappings v, source_documents p where q.index_variant_mapping_id = v.id and p.id=source_document_id order by document_year desc");

		while(rs.next()) {
			String svg_display_name = rs.getString("svg_display_name");
			String index_variant_name = rs.getString("name");
			String show_in_svg = rs.getString("show_in_svg");
			int index_variant_mapping_id = rs.getInt("vm_id");
			int index_variant_location_start = rs.getInt("start_1based");
			int index_variant_location_end = rs.getInt("end_1based");
			String eqtl_marker_equivalence_set_id = rs.getString("eqtl_marker_equivalence_set_id");
			if (rs.wasNull()) eqtl_marker_equivalence_set_id = "";
			boolean is_coding = false;
			ResultSet rs2 = statement2.executeQuery("select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+index_variant_mapping_id+") and is_coding_impact=1");
			while (rs2.next()) {
				int chk = rs2.getInt(1);
				if (chk>0) is_coding = true;
			}
			rs2.close();
			String effect_allele = rs.getString("effect_allele");
			if (rs.wasNull()) effect_allele = "";
			String tissue = rs.getString("tissue");
			tissue = tissue.replaceAll("_", " ");
			String gsymbol = rs.getString("gene_symbol");
			String column_display_text = rs.getString("column_display_text");
			String outbound_link = rs.getString("outbound_link");
			String pvalue = String.format("%.2e", Double.parseDouble(rs.getString("pvalue")));
			String beta = String.format("%.3f", Double.parseDouble(rs.getString("beta")));
			int document_year = rs.getInt("document_year");
			String myb = beta;
			try {
				BigDecimal bd1 = new BigDecimal(beta);
				bd1 = bd1.setScale(2, RoundingMode.HALF_UP);
				myb = bd1.toString();
			} catch (Exception eee) {
				myb = beta;
			}
			beta = myb;
			String curator_comment = rs.getString("curator_comment"); if (rs.wasNull()) curator_comment = "";
			int eqtl_table_row_id = rs.getInt("id");
			IndexVariant giv;
			String vkey = index_variant_name+" "+index_variant_location_start+" "+index_variant_location_end;
			if (!gwas_eqtl_index_variants.contains(vkey)) {
				gwas_eqtl_index_variants.add(vkey);
				giv = new IndexVariant();
				giv.index_variant_name = index_variant_name;
				giv.index_variant_location_start = index_variant_location_start;
				giv.index_variant_location_end = index_variant_location_end;
				giv.is_coding = is_coding;
				assoc_result_hash.put(vkey, giv);
			} else {
				giv = assoc_result_hash.get(vkey);
			}

			EQTLResult er = new EQTLResult();
			er.svg_display_name = svg_display_name;
			er.index_variant_name = index_variant_name;
			er.index_variant_start = index_variant_location_start;
			er.index_variant_end = index_variant_location_end;
			er.column_display_text = column_display_text;
			er.outbound_link = outbound_link;
			er.document_year = document_year;
			er.effect_allele = effect_allele;
			er.beta = beta;
			er.pvalue = pvalue;
			er.tissue = tissue;
			er.gene_symbol = gsymbol;
			er.eqtl_table_row_id = eqtl_table_row_id;
			er.curator_comment = curator_comment;
			if (show_in_svg.equals("1")) er.show_in_svg = true;
			er.eqtl_marker_equivalence_set_id = eqtl_marker_equivalence_set_id;
			giv.eqtl_results.add(er);
			all_eqtl_results.add(er);

			if (er.show_in_svg) {
				LDGMarker lm = null;
				if (ldgmarkers.containsKey(Integer.valueOf(index_variant_mapping_id))) {
					lm = ldgmarkers.get(Integer.valueOf(index_variant_mapping_id));
				} else {
					lm = new LDGMarker();
					lm.id = index_variant_mapping_id;
					lm.name = index_variant_name;
					lm.start_1based = index_variant_location_start;
					ldgmarkers.put(Integer.valueOf(index_variant_mapping_id), lm);
					Integer ii = Integer.valueOf(index_variant_mapping_id);
					int idx = ldglookup.indexOf(ii);
					if (idx==-1) {
						idx = ldglookup.size();
						ldglookup.addElement(ii);
					}
					ldgmarkersbs.set(idx);
				}
				lm.eqtl_results.add(er);
			}	

			rs2 = statement2.executeQuery("select name from meta_resources where id = (select meta_resource_id from variant_mapping_eqtl_results where id = "+er.eqtl_table_row_id+")");
			while (rs2.next()) {
				String nm = rs2.getString("name");
				if (nm.indexOf("Ensembl")>-1) er.automatic = true;
			}
			rs2.close();				
			rs2 = statement2.executeQuery("select max(frequency) from variant_mapping_allele_frequencies where reference_dataset_id = (select id from reference_datasets where name = '1000GENOMES:phase_3:EUR') and variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+index_variant_mapping_id+")");
			while (rs2.next()) {
				Double mf = rs2.getDouble(1);
				BigDecimal bd1 = new BigDecimal(1.0-mf);
				bd1 = bd1.setScale(3, RoundingMode.HALF_UP);
				er.sum_1kgp3eur_maf_str = bd1.toString();
			}
			rs2.close();
			
			if (!er.eqtl_marker_equivalence_set_id.equals("")) {
				rs2 = statement2.executeQuery("select name from reference_datasets where id = (select reference_dataset_id from eqtl_marker_equivalence_sets where id = "+er.eqtl_marker_equivalence_set_id+")");
				while (rs2.next()) {
					String nm = rs2.getString("name");
					er.marker_equivalence_set_label = "LD "+nm;
				}
				rs2.close();
				rs2 = statement2.executeQuery("select count(*) from eqtl_marker_equivalence_set_members where eqtl_marker_equivalence_set_id = "+er.eqtl_marker_equivalence_set_id+" and r2_to_index_variant>=0.6");
				while (rs2.next()) {
					String cnt = rs2.getString(1);
					er.marker_equivalence_set_count_label = "("+cnt+")";
				}
				rs2.close();
				rs2 = statement2.executeQuery("select v.id as vid, v.name as name, v.start_1based as start_1based, v.end_1based as end_1based, r2_to_index_variant, pvalue, beta from variant_mappings v, eqtl_marker_equivalence_set_members where eqtl_marker_equivalence_set_id = "+er.eqtl_marker_equivalence_set_id+" and v.id = variant_mapping_id and r2_to_index_variant>=0.6 order by v.start_1based");			
				while (rs2.next()) {
					String vid = rs2.getString("vid");
					int s1b = rs2.getInt("start_1based");
					int e1b = rs2.getInt("end_1based");
					String vn = rs2.getString("name");
					String r2 = rs2.getString("r2_to_index_variant");
					String pv = rs2.getString("pvalue");
					String vbeta = rs2.getString("beta");
					boolean v_is_coding = false;
					ResultSet rs3 = statement3.executeQuery("select count(*) from variant_mapping_alternate_allele_impacts where variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+vid+") and is_coding_impact=1");
					while (rs3.next()) {
						int chk = rs3.getInt(1);
						if (chk>0) v_is_coding = true;
					}
					rs3.close();
					er.ld_locs_starts.add(Integer.valueOf(s1b));
					er.ld_locs_ends.add(Integer.valueOf(e1b));
					er.ld_variants.add(vn);
					er.ld_r2.add(r2);
					er.ld_pv.add(pv);
					er.ld_beta.add(vbeta);
					if (v_is_coding) er.ld_coding.add("1");
					else er.ld_coding.add("0");
				}
				rs2.close();	
			} else {
				er.marker_equivalence_set_label = "None";
			}
			
			er.association_overlap_count = 0;
			for (int i=0; i<all_gwas_results.size(); ++i) {
				GWASResult gw = all_gwas_results.get(i);
				if (gw.show_in_svg && !gw.marker_equivalence_set.equals("Unset")) {
					boolean found = false;
					if (er.index_variant_name.equals(gw.index_variant_name) && er.index_variant_start==gw.index_variant_start && er.index_variant_end==gw.index_variant_end) {
						er.association_overlap_count++;
						continue;
					}
					for (int j=0; j<gw.mes_variants.size(); ++j) {
						String var = gw.mes_variants.get(j);
						Integer vend = gw.mes_locs_ends.get(j);
						Integer vstart = gw.mes_locs_starts.get(j);
						if (er.index_variant_name.equals(var) && er.index_variant_start==vstart && er.index_variant_end==vend) {
							found = true;
							er.association_overlap_count++;
							break;
						}
					}
					if (found) continue;
					for (int k=0; k<er.ld_variants.size(); ++k) {
						String ev = er.ld_variants.get(k);
						Integer eend = er.ld_locs_ends.get(k);
						Integer estart = er.ld_locs_starts.get(k);
						if (ev.equals(gw.index_variant_name) && eend==gw.index_variant_end && estart==gw.index_variant_start) {
							er.association_overlap_count++;
							found = true;
							break;
						}
						for (int j=0; j<gw.mes_variants.size(); ++j) {
							String var = gw.mes_variants.get(j);
							Integer vend = gw.mes_locs_ends.get(j);
							Integer vstart = gw.mes_locs_starts.get(j);
							if (ev.equals(var) && eend==vend && estart==vstart) {
								found = true;
								er.association_overlap_count++;
								break;
							}
						}
						if (found) break;
					}
				}
			}
		}
		rs.close();

		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done collecting eqtl "+delta+" ms");
		pmillis = cmillis;

		// phenotype associations
		
		Vector<GenePhenAssoc> genephenassocs = new Vector<>();
		rs = statement.executeQuery("select id, phenotype, gene_symbol, source, source_linkout, curator_comment from gene_phenotype_associations order by phenotype");
		while(rs.next()) {
			String id = rs.getString("id");
			String ph = rs.getString("phenotype");
			String gene = rs.getString("gene_symbol");
			String source = rs.getString("source");
			String link = rs.getString("source_linkout");
			if (rs.wasNull()) link = "";
			String comment = rs.getString("curator_comment");
			if (rs.wasNull()) comment = "";
			GenePhenAssoc gp = new GenePhenAssoc();
			gp.db_id = id;
			gp.phenotype = ph;
			gp.gene = gene;
			gp.source = source;
			gp.linkout = link;
			gp.curator_comment = comment;
			genephenassocs.add(gp);
		}
		rs.close();
		
		Vector<PhenAlleleAssoc> phenalleleassocs = new Vector<>();
		rs = statement.executeQuery("select variant_mapping_id, name, phenotype, source, source_linkout, unaudited_risk_allele, clinical_significance from allele_phenotype_associations, variant_mappings where variant_mappings.id=variant_mapping_id order by phenotype");
		while(rs.next()) {
			String ph = rs.getString("phenotype");
			String vmid = rs.getString("variant_mapping_id");
			String variant = rs.getString("name");
			String source = rs.getString("source");
			String link = rs.getString("source_linkout");
			if (rs.wasNull()) link = "";
			String unaudited_risk_allele = rs.getString("unaudited_risk_allele");
			if (rs.wasNull()) unaudited_risk_allele = "";
			String clinical_significance = rs.getString("clinical_significance");
			if (rs.wasNull()) clinical_significance = "";
			Vector<String> symbols = new Vector<>();
			ResultSet rs2 = statement2.executeQuery("select ta.symbol as symbol from transcript_annotations ta, variant_mapping_alternate_allele_impacts vmaai where ta.id = vmaai.transcript_annotation_id and variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+vmid+") order by symbol");
			while (rs2.next()) {
				String sy = rs2.getString("symbol");
				if (!symbols.contains(sy)) symbols.add(sy);
			}
			rs2.close();
			String gs = "";
			for (int jj=0; jj<symbols.size(); ++jj) {
				if (jj>0) gs+=", ";
				gs+=symbols.get(jj);
			}
			PhenAlleleAssoc pa = new PhenAlleleAssoc();
			pa.phenotype = ph;
			pa.source = source;
			pa.variant = variant;
			pa.linkout = link;
			pa.gene_string = gs;
			pa.unaudited_risk_allele = unaudited_risk_allele;
			pa.clinical_significance = clinical_significance;
			phenalleleassocs.add(pa);
		}
		rs.close();
	
		// construct LD groups

		int ldgmarkers_max_id = ldgmarkersbs.length()-1;
		BitSet[] ldghighr2bs = new BitSet[ldgmarkers_max_id+1];
		for (int j=0; j<=ldgmarkers_max_id; ++j) {
			ldghighr2bs[j] = new BitSet();
			ldghighr2bs[j].set(j); // for identity
		}

		rs = statement.executeQuery("select variant_mapping_id1, variant_mapping_id2 from r2_values where r2_value >= 0.6 and reference_dataset_id = (select id from reference_datasets where name = '1000GENOMES:phase_3:EUR')");
		while(rs.next()) {
			int vmi1 = rs.getInt("variant_mapping_id1");
			Integer i1 = Integer.valueOf(vmi1);
			int idx1 = ldglookup.indexOf(i1);
			if (idx1>-1) {
				int vmi2 = rs.getInt("variant_mapping_id2");
				Integer i2 = Integer.valueOf(vmi2);
				int idx2 = ldglookup.indexOf(i2);
				if (idx2>-1) {
					if (idx2<idx1) {
						int t = idx1;
						idx1 = idx2;
						idx2 = t;
					}
					ldghighr2bs[idx1].set(idx1);
					ldghighr2bs[idx2].set(idx2);
					ldghighr2bs[idx1].set(idx2);
				}
			}
		}
		rs.close();
		
		Vector<BitSet> groups = new Vector<>();
		BitSet done = new BitSet();
		for (int j=0; j<=ldgmarkers_max_id; ++j) {
			if (ldgmarkersbs.get(j)) {
				if (!done.get(j)) {
					BitSet newbs = (BitSet)ldghighr2bs[j];
					done.set(j);
					for (int k=j+1; k<=ldgmarkers_max_id; ++k) {
						if (ldgmarkersbs.get(k)) {
							if (!done.get(k)) {
								BitSet bsc = (BitSet)newbs.clone();
								bsc.and(ldghighr2bs[k]);
								if (!bsc.isEmpty()) {
									newbs.or(ldghighr2bs[k]);
									done.set(k);
								}
							}
						}
					}
					groups.add(newbs);
				}
			}
		}
		
		for (int j=0; j<groups.size(); ++j) {
			BitSet bs = groups.get(j);
			LDGroup ldg = new LDGroup();
			int start_pos_total = 0;
			ldg.left_edge_svg_location = -1;
			for (int k=0; k<bs.length(); ++k) {
				if (bs.get(k)) {
					LDGMarker lm = ldgmarkers.get(ldglookup.get(k));
					start_pos_total += lm.start_1based;
					boolean added = false;
					for (int jk=0; jk<ldg.markers.size(); ++jk) {
						LDGMarker lm2 = ldg.markers.get(jk);
						if (lm2.start_1based>lm.start_1based) {
							ldg.markers.add(jk, lm);
							added = true;
							break;
						}
					}
					if (!added) ldg.markers.add(lm);
				}
			}
			ldg.avg_start_1based = (double)start_pos_total/(double)ldg.markers.size();
			boolean gadded = false;
			for (int jj=0; jj<ldgroups.size(); ++jj) {
				LDGroup ldg2 = ldgroups.get(jj);
				if (ldg2.avg_start_1based> ldg.avg_start_1based) {
					ldgroups.add(jj, ldg);
					gadded = true;
					break;
				}
			}
			if (!gadded) {
				ldgroups.add(ldg);
			}
		}
		
		for (int j=0; j<ldgroups.size(); ++j) {
			LDGroup ldg = ldgroups.get(j);
			for (int k=0; k<ldg.markers.size(); ++k) {
				LDGMarker lm = ldg.markers.get(k);
				rs = statement.executeQuery("select count(*) from variant_mapping_allele_frequencies where reference_dataset_id = (select id from reference_datasets where name = '1000GENOMES:phase_3:EUR') and variant_mapping_allele_id in (select id from variant_mapping_alleles where variant_mapping_id = "+lm.id+")");
				while(rs.next()) {
					String cnt = rs.getString(1);
					if (!cnt.equals("0")) {
						lm.ld_data_likely_exist = true;
					}
				}
				rs.close();
			}
		}

		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done making ldgroups "+delta+" ms");
		pmillis = cmillis;

		statement.close();
		statement2.close();
		statement3.close();
		connection.close();

		// done gathering information
		// start making output
		
		Layout layout = new Layout();
		layout.width_in_bases = (chrom_high - chrom_low)+1;
		layout.svg_units_per_base = (double)layout.svg_width/(double)layout.width_in_bases;
		layout.chrom_low = chrom_low;
		layout.chrom_high = chrom_high;
		layout.tss = tss;
		layout.tss_dir = tss_dir;
		layout.PlaceVertical(TargetAnnotation);
		for (int i=0; i<OverlappingGenes.size(); ++i) {
			GeneAnnotation g = OverlappingGenes.get(i);
			layout.PlaceVertical(g);
		}
		layout.svg_height = (layout.minus_footprints.size()+layout.plus_footprints.size())*layout.gene_line_height;
		int gwas_extra = 0;
		for (int i=0; i<gwas_eqtl_index_variants.size(); ++i) {
			IndexVariant giv = assoc_result_hash.get(gwas_eqtl_index_variants.get(i));
			Hashtable<String, String> found = new Hashtable<>();
			for (int j=0; j<giv.gwas_results.size(); ++j) {
				GWASResult g = giv.gwas_results.get(j);
				if (!g.marker_equivalence_set.equals("Unset") && g.show_in_svg) { // will be shown
					gwas_extra+=20; // we have to show the summary text somewhere
					if (!found.containsKey(g.marker_equivalence_set) || g.marker_equivalence_set.equals("Credible set")) {
						found.put(g.marker_equivalence_set, "1");
						gwas_extra+=20; // we'll have to add the LD line
					}
				}
			}
			for (int j=0; j<giv.eqtl_results.size(); ++j) {
				EQTLResult q = giv.eqtl_results.get(j);
				if (q.show_in_svg) {
					gwas_extra+=20; // we have to show the summary text somewhere
					gwas_extra+=20; // we'll have to add the LD line
				}
			}	
		}

		int ld_extra = -1;
		layout.gap_count = 2 + (ldgroups.size()-1);
		int max_squares = 0;
		for (int i=0; i<ldgroups.size(); ++i) {
			LDGroup ldg = ldgroups.get(i);
			layout.block_count+=ldg.markers.size();
			for (int j=0; j<ldg.markers.size(); ++j) {
				LDGMarker lm = ldg.markers.get(j);
				int b_ctr=0;
				for (int k=0; k<lm.coding_impacts.size(); ++k) {
					b_ctr++;
				}
				for (int k=0; k<lm.gwas_results.size(); ++k) {
					b_ctr++;
				}
				for (int k=0; k<lm.eqtl_results.size(); ++k) {
					b_ctr++;
				}
				if (b_ctr>max_squares) max_squares = b_ctr;
			}
		}
		layout.square_size = (int)(layout.svg_width/(layout.gap_count+layout.block_count));
		if (layout.square_size>60) layout.square_size = 60;
		ld_extra = max_squares*layout.square_size+layout.box_start_y_offset+5;
		int largest_extra = ld_extra;
		if (gwas_extra>ld_extra) largest_extra = gwas_extra;

		layout.svg_height+=largest_extra+layout.kb_track_height;
		layout.first_plus_strand_gene_top = (layout.plus_footprints.size()-1)*layout.gene_line_height+layout.kb_track_height;
		layout.first_minus_strand_gene_top = (layout.plus_footprints.size())*layout.gene_line_height+layout.kb_track_height;

		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done with pre-layout calcs "+delta+" ms");
		pmillis = cmillis;

		// start making SVG
		
		Document document = SvgGraphics.getDocument();
		Element svg = SvgGraphics.makeNode(
				document,
				null,//parent
				"svg",//node type
				new String[]{"xmlns","http://www.w3.org/2000/svg",
						"xmlns:xlink","http://www.w3.org/1999/xlink",
						"version","1.1",
						"preserveAspectRatio","none",
						"width",""+layout.svg_width,
						"height",""+layout.svg_height,
						"id", "mysvg",
						"viewBox", "0 0 "+layout.svg_width+" "+layout.svg_height
				});
		// "xmlns:xlink","http://www.w3.org/2000/xlink",  .. 1999 seems to work on svg DOMparser re-import, whereas 2000 doesn't (?!?!)

		String bcol = "#fcfcfc";
		SvgGraphics.makeNode(document,
				svg,//parent
				"rect",//type
				new String[]{"x","0",
						"y","0",
						"width",""+layout.svg_width,
						"height",""+layout.svg_height,
						"id", "mybackground",
						"onmousedown", "ShowLocation(evt,1)",
						"onmousemove", "ShowLocation(evt,0)",
						"onmouseup", "HideTooltip(evt)",
						"onmouseout", "HideTooltip(evt)",
						"cursor","default",
						"fill",bcol
		});

		layout.TranscriptLayout(TargetAnnotation, document, svg);
		for (int i=0; i<OverlappingGenes.size(); ++i) {
			GeneAnnotation g = OverlappingGenes.get(i);
			layout.TranscriptLayout(g, document, svg);
		}	
		layout.AssocLayout(gwas_eqtl_index_variants, assoc_result_hash, document, svg, svg_mode);
		layout.LDGroupLayout(ldgroups, document, svg, svg_mode, target_name);
		layout.KBTrackLayout(TargetAnnotation,  document, svg);
		
		cmillis = System.currentTimeMillis();
		delta = cmillis - pmillis;		
		if (output_profile) System.out.println("done with svg "+delta+" ms");
		pmillis = cmillis;		

		// Generate HTML
		
		sb.append(GetHTMLStart("TGN - "+gene_symbol.toUpperCase(),backend_version,db_version));
		

		// svg
		
		sb.append("<div id='main'>\n");
		sb.append("<div id=\"paper1\" align=\"center\">\n");

		CDATASection cdata = document.createCDATASection(CreateCDATAFxns.getCDATAFxns(gene_symbol, layout));
		Element scriptelement = SvgGraphics.makeNode(document,
				svg,//parent
				"script",//type
				new String[]{"type","text/ecmascript"
		});
		scriptelement.appendChild(cdata);
		SvgGraphics.makeNode(document,
				svg,//parent
				"rect",//type
				new String[]{"x","0",
						"y","0",
						"width","55",
						"height","17",
						"id", "tooltip_bg",
						"fill","#cccccc",
						"opacity","0.9",
						"visibility", "hidden"
		});
		Element tt = SvgGraphics.makeNode(document,
				svg,//parent
				"text",//type
				new String[]{"x","0",
						"y","0",
						"id", "tooltip",
						"visibility", "hidden",
						"font-size","12px",
						"font-weight","400",
						"font-family","Lato, Helvetica, Arial, sans-serif"
		});
		tt.setTextContent("Tooltip");
		try {
			// Transformer handles XML escaping
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			String xml_string = writer.getBuffer().toString();
			sb.append(xml_string+"\n");
			if (is_reset_if_1>1) {return xml_string;}
		} catch (Exception e) {	    
			e.printStackTrace();
		}
		sb.append("</div>\n");

		// various sometimes-seen items

		sb.append("<div id=\"ak_supl_div\" align=\"center\" style=\"margin-top:10px;\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"crediblesetoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addeqtloverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"adddetailoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addpubmedoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addbiorxivoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addfileoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addweboverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");
		sb.append("<div id=\"addgwasoverlay\" align=\"center\" class=\"akmodal\">\n");
		sb.append("</div>\n");

		// svg controls
		
		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("View Mode ");
		sb.append("  <input type=\"radio\" onclick=\"UpdateSVGMode()\" name=\"svg_display_mode\" value=\"Credible Sets\" checked> Credible Sets\n");
		sb.append("  <input type=\"radio\" onclick=\"UpdateSVGMode()\" name=\"svg_display_mode\" value=\"LD Summary\"> LD Summary (0.6 r2 threshold, 1KGp3:EUR)\n");	
		String hckd = "";
		if (hidenoncoding==1) {hckd = "checked";}
		sb.append("  <input style=\"margin-left:50px;\" type=\"checkbox\" onclick=\"UpdateShowHideNonCoding(this)\" id=\"showhidenoncodingcheck\" "+hckd+"><label for=showhidenoncodingcheck>Hide non-coding genes</label>\n");
		sb.append("</div>\n");		

		// project summary

		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		String sstring = " ("+target_name+" TSS "+resource_name+":chr"+chromosome+":"+NumberFormat.getNumberInstance(Locale.US).format(tss)+")";
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">Summary"+sstring+"</h3>\n");
		sb.append("<div>\n");
		sb.append("<div style=\"width:100%\">\n");
		sb.append("<textarea placeholder=\"Enter summary\" rows=\"36\" id=\"aksummaryarea\" onkeyup=\"startupdatesummarytimer()\" class=\"form-control\" style=\"width:100%; font-family: Lato, Sans-serif; font-size:12px; line-height:18px\">"+StringEscapeUtils.escapeHtml4(summary)+"</textarea>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");

		// gwas

		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">Association Results</h3>\n");
		sb.append("<div class=\"akwrapper\">\n");
		sb.append("<table id='gwastable' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("<thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">Display Name</th>\n");
		sb.append("    <th class=\"resizable-false\">Phenotype</th>\n");
		sb.append("    <th class=\"resizable-false\">Source</th>\n");
		sb.append("    <th class=\"resizable-false\">Year</th>\n");
		sb.append("    <th class=\"resizable-false\">Index<br>Variant</th>\n");
		sb.append("    <th class=\"resizable-false\">Allele</th>\n");		
		sb.append("    <th class=\"resizable-false\">Pvalue</th>\n");
		sb.append("    <th class=\"resizable-false\">OR/Beta</th>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Credible Set</th>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Show</th>\n");
		sb.append("    <th class=\"resizable-false\">Curator Comment</th>\n");	
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");			
		sb.append("  </tr>\n");
		sb.append("</thead>\n");
		sb.append("<tbody>\n");
		for (int j=0; j<all_gwas_results.size(); ++j) {
			GWASResult g = all_gwas_results.get(j);
			if (g.is_pqtl) continue;
			String rowclass = "akgwasrowclass"+g.gwas_table_row_id;
			sb.append("  <tr id=\'gwasrow"+g.gwas_table_row_id+"\'>\n");
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_gwas_sdn ak_tablecell "+rowclass+"\" style=\"top:0; left:0; right:0; bottom:0; position:absolute; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+g.gwas_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"startgwassvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(g.svg_display_name)+"</textarea></td>\n");	
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(g.trait)+"</td>\n");
			if (!g.outbound_link.equals("")) sb.append("    <td class=\"ak_tablecell\"><a href=\""+g.outbound_link+"\" target=\"_blank\">"+g.column_display_text+"</a></td>\n");
			else sb.append("    <td class=\"ak_tablecell\">"+g.column_display_text+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.document_year))+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(g.index_variant_name)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(g.index_variant_name)+"</a></td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(g.allele)+g.allele_sup+"</td>\n");			
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.pvalue))+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.or_beta))+"</td>\n");
			sb.append("    <td><select class=\"ak_mes_selector "+rowclass+"\" style=\"width:100%\" onchange=\"UpdateGWASMarkerEquivalenceSet(this)\" data-dbid=\""+g.gwas_table_row_id+"\">\n");
			if (g.marker_equivalence_set.equals("Unset")) sb.append("      <option selected=\"selected\" value=\"Unset\">Unset</option>\n");
			else sb.append("      <option value=\"Unset\">Unset</option>\n");
			if (g.marker_equivalence_set.equals("None")) sb.append("      <option selected=\"selected\" value=\"None\">None</option>\n");
			else sb.append("      <option value=\"None\">None</option>\n");
			for (int ij=0; ij<g.LDdatasets.size(); ++ij) {
				String ds = g.LDdatasets.get(ij);
				String dsr = ds.replace("1000GENOMES:phase_3", "1KGp3");
				String dsc = g.LDdatasetsHighR2Count.get(ij);
				if (g.marker_equivalence_set.equals(ds)) sb.append("      <option selected=\"selected\" value=\""+StringEscapeUtils.escapeHtml4(ds)+"\">"+StringEscapeUtils.escapeHtml4(dsr)+" ("+dsc+")</option>\n");
				else sb.append("      <option value=\""+StringEscapeUtils.escapeHtml4(ds)+"\">"+StringEscapeUtils.escapeHtml4(dsr)+" ("+dsc+")</option>\n");
			}
			if (g.credible_set_name.equals("")) {
				sb.append("      <option value=\"Add credible set\">Add credible set</option>\n");
			} else {
				if (g.marker_equivalence_set.equals("Credible set")) {
					sb.append("      <option selected=\"selected\" value=\"Credible set\">"+StringEscapeUtils.escapeHtml4(g.credible_set_name)+" ("+g.credible_set_member_count+")</option>\n");		
				} else {
					sb.append("      <option value=\"Credible set\">"+StringEscapeUtils.escapeHtml4(g.credible_set_name)+" ("+g.credible_set_member_count+")</option>\n");
				}
				sb.append("      <option value=\"Delete credible set\">Delete credible set</option>\n");
			}	
			sb.append("    </select></td>\n");
			String ckd = "";
			if (g.show_in_svg) ckd = "checked";
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateGWASShowHide(this)\" data-dbid=\""+g.gwas_table_row_id+"\" "+ckd+"></td>\n");
			sb.append("    <td style=\"position:relative;\"><textarea class=\"ak_gwas_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+g.gwas_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"startgwascommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(g.curator_comment)+"</textarea></td>\n");
			if (!g.automatic) sb.append("    <td style=\"vertical-align: middle;\"><button id=\"x_gwas_"+g.gwas_table_row_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveGWASRow('gwasrow"+g.gwas_table_row_id+"', 0)\">X</button></td>");
			else sb.append("    <td style=\"vertical-align: middle;\"></td>");
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");
		sb.append("</table>\n");
		sb.append("<div id=\"gwas_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("<button style=\"margin-left: 200px;\" class=\"ak_table_button\" id=\"gwas_add_button\" onclick=\"AddGWASOverlay(0)\">Add Association</button>\n");
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");

		// gwas details
		
		sb.append(CreateDetailAccordion("associationDetailsAccordion", "Association Details", "associationDetailsDiv", "associationdetailstable", allsourcedocuments, "Association", "association_details_pager"));
		
		
		// pqtl

		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">pQTL Results</h3>\n");
		sb.append("<div class=\"akwrapper\">\n");
		sb.append("<table id='pqtltable' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("<thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">Display Name</th>\n");
		sb.append("    <th class=\"resizable-false\">Phenotype</th>\n");
		sb.append("    <th class=\"resizable-false\">Source</th>\n");
		sb.append("    <th class=\"resizable-false\">Year</th>\n");
		sb.append("    <th class=\"resizable-false\">Index<br>Variant</th>\n");
		sb.append("    <th class=\"resizable-false\">Allele</th>\n");		
		sb.append("    <th class=\"resizable-false\">Pvalue</th>\n");
		sb.append("    <th class=\"resizable-false\">OR/Beta</th>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Credible Set</th>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Show</th>\n");
		sb.append("    <th class=\"resizable-false\">Curator Comment</th>\n");	
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");			
		sb.append("  </tr>\n");
		sb.append("</thead>\n");
		sb.append("<tbody>\n");
		for (int j=0; j<all_gwas_results.size(); ++j) {
			GWASResult g = all_gwas_results.get(j);
			if (!g.is_pqtl) continue;
			String rowclass = "akgwasrowclass"+g.gwas_table_row_id;
			sb.append("  <tr id=\'gwasrow"+g.gwas_table_row_id+"\'>\n");
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_gwas_sdn ak_tablecell "+rowclass+"\" style=\"top:0; left:0; right:0; bottom:0; position:absolute; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+g.gwas_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"startgwassvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(g.svg_display_name)+"</textarea></td>\n");	
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(g.trait)+"</td>\n");
			if (!g.outbound_link.equals("")) sb.append("    <td class=\"ak_tablecell\"><a href=\""+g.outbound_link+"\" target=\"_blank\">"+g.column_display_text+"</a></td>\n");
			else sb.append("    <td class=\"ak_tablecell\">"+g.column_display_text+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.document_year))+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(g.index_variant_name)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(g.index_variant_name)+"</a></td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(g.allele)+g.allele_sup+"</td>\n");			
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.pvalue))+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(g.or_beta))+"</td>\n");
			sb.append("    <td><select class=\"ak_mes_selector "+rowclass+"\" style=\"width:100%\" onchange=\"UpdateGWASMarkerEquivalenceSet(this)\" data-dbid=\""+g.gwas_table_row_id+"\">\n");
			if (g.marker_equivalence_set.equals("Unset")) sb.append("      <option selected=\"selected\" value=\"Unset\">Unset</option>\n");
			else sb.append("      <option value=\"Unset\">Unset</option>\n");
			if (g.marker_equivalence_set.equals("None")) sb.append("      <option selected=\"selected\" value=\"None\">None</option>\n");
			else sb.append("      <option value=\"None\">None</option>\n");
			for (int ij=0; ij<g.LDdatasets.size(); ++ij) {
				String ds = g.LDdatasets.get(ij);
				String dsr = ds.replace("1000GENOMES:phase_3", "1KGp3");
				String dsc = g.LDdatasetsHighR2Count.get(ij);
				if (g.marker_equivalence_set.equals(ds)) sb.append("      <option selected=\"selected\" value=\""+StringEscapeUtils.escapeHtml4(ds)+"\">"+StringEscapeUtils.escapeHtml4(dsr)+" ("+dsc+")</option>\n");
				else sb.append("      <option value=\""+StringEscapeUtils.escapeHtml4(ds)+"\">"+StringEscapeUtils.escapeHtml4(dsr)+" ("+dsc+")</option>\n");
			}
			if (g.credible_set_name.equals("")) {
				sb.append("      <option value=\"Add credible set\">Add credible set</option>\n");
			} else {
				if (g.marker_equivalence_set.equals("Credible set")) {
					sb.append("      <option selected=\"selected\" value=\"Credible set\">"+StringEscapeUtils.escapeHtml4(g.credible_set_name)+" ("+g.credible_set_member_count+")</option>\n");		
				} else {
					sb.append("      <option value=\"Credible set\">"+StringEscapeUtils.escapeHtml4(g.credible_set_name)+" ("+g.credible_set_member_count+")</option>\n");
				}
				sb.append("      <option value=\"Delete credible set\">Delete credible set</option>\n");
			}	
			sb.append("    </select></td>\n");
			String ckd = "";
			if (g.show_in_svg) ckd = "checked";
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateGWASShowHide(this)\" data-dbid=\""+g.gwas_table_row_id+"\" "+ckd+"></td>\n");
			sb.append("    <td style=\"position:relative;\"><textarea class=\"ak_gwas_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+g.gwas_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"startgwascommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(g.curator_comment)+"</textarea></td>\n");
			if (!g.automatic) sb.append("    <td style=\"vertical-align: middle;\"><button id=\"x_gwas_"+g.gwas_table_row_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveGWASRow('gwasrow"+g.gwas_table_row_id+"', 1)\">X</button></td>");
			else sb.append("    <td style=\"vertical-align: middle;\"></td>");
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");
		sb.append("</table>\n");
		sb.append("<div id=\"pqtl_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("<button style=\"margin-left: 200px;\" class=\"ak_table_button\" id=\"pqtl_add_button\" onclick=\"AddGWASOverlay(1)\">Add pQTL</button>\n");
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");

		// pqtl details
		
		sb.append(CreateDetailAccordion("pqtlDetailsAccordion", "pQTL Details", "pqtlDetailsDiv", "pqtldetailstable", allsourcedocuments, "PQTL", "pqtl_details_pager"));
		
	
		
		
		
		
		
		// eqtl

		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">eQTL Results</h3>\n");
		sb.append("<div class=\"akwrapper\">\n");
		sb.append("<table id='eqtltable' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("<thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">Display Name</th>\n");
		sb.append("    <th class=\"resizable-false\">Tissue</th>\n");
		sb.append("    <th class=\"resizable-false\">Gene</th>\n");		
		sb.append("    <th class=\"resizable-false\">Source</th>\n");
		sb.append("    <th class=\"resizable-false\">Year</th>\n");
		sb.append("    <th class=\"resizable-false\">Index<br>Variant</th>\n");	
		sb.append("    <th class=\"resizable-false\">Pvalue</th>\n");
		sb.append("    <th class=\"resizable-false\">Beta</th>\n");
		sb.append("    <th class=\"resizable-false\">Effect<br>Allele</th>\n");
		sb.append("    <th class=\"resizable-false\">Credible Set</th>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Show</th>\n");
		sb.append("    <th class=\"resizable-false\">Curator Comment</th>\n");	
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");	
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");	
		sb.append("  </tr>\n");
		sb.append("</thead>\n");
		sb.append("<tbody>\n");
		for (int j=0; j<all_eqtl_results.size(); ++j) {
			EQTLResult q = all_eqtl_results.get(j);
			String rowclass = "akeqtlrowclass"+q.eqtl_table_row_id;
			sb.append("  <tr id=\'eqtlrow"+q.eqtl_table_row_id+"\'>\n");
			//sb.append("    <td><textarea class=\"ak_eqtl_sdn ak_tablecell "+rowclass+"\" style=\"width:145px; height:100%\" data-dbid=\""+q.eqtl_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"starteqtlsvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(q.svg_display_name)+"</textarea></td>\n");
			sb.append("    <td style=\"position:relative;\"><textarea class=\"ak_eqtl_sdn ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+q.eqtl_table_row_id+"\" placeholder=\"Enter display name\" onkeyup=\"starteqtlsvgdisplaynametimer(this)\">"+StringEscapeUtils.escapeHtml4(q.svg_display_name)+"</textarea></td>\n");
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.tissue)+"</td>\n");
			sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.gene_symbol)+"</td>\n");
			if (!q.outbound_link.equals("")) sb.append("    <td class=\"ak_tablecell\"><a href=\""+q.outbound_link+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(q.column_display_text)+"</a></td>\n");
			else sb.append("    <td class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.column_display_text)+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(String.valueOf(q.document_year))+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(q.index_variant_name)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(q.index_variant_name)+"</a></td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.pvalue)+"</td>\n");			
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.beta)+"</td>\n");
			sb.append("    <td style=\"text-align:center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(q.effect_allele)+"</td>\n");
			String dsr = q.marker_equivalence_set_label.replace("1000GENOMES:phase_3", "1KGp3");
			if (q.marker_equivalence_set_count_label.equals("")) {
				sb.append("    <td style=\"text-align:left;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(dsr)+"</td>\n");
			} else {
				sb.append("    <td style=\"text-align:left;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(dsr+" "+q.marker_equivalence_set_count_label)+"</td>\n");
			}
			String ckd = "";
			if (q.show_in_svg) ckd = "checked";
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateEQTLShowHide(this)\" data-dbid=\""+q.eqtl_table_row_id+"\" "+ckd+"></td>\n");
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_eqtl_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+q.eqtl_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"starteqtlcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(q.curator_comment)+"</textarea></td>\n");
			//sb.append("    <td><textarea class=\"ak_eqtl_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+q.eqtl_table_row_id+"\" placeholder=\"Enter comment\" onkeyup=\"starteqtlcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(q.curator_comment)+"</textarea></td>\n");
			if (!q.automatic) sb.append("    <td style=\"vertical-align: middle;\"><button id=\"x_eqtl_"+q.eqtl_table_row_id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveEQTLRow('eqtlrow"+q.eqtl_table_row_id+"')\">X</button></td>");
			else sb.append("    <td style=\"vertical-align: middle;\"></td>");
			sb.append("    <td id=\'eqtloverlap"+q.eqtl_table_row_id+"\' style=\"text-align:center;\" class=\"ak_tablecell\">"+q.association_overlap_count+"</td>\n");			
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");
		sb.append("</table>\n");
		sb.append("<div id=\"eqtl_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("<button style=\"margin-left: 200px;\" class=\"ak_table_button\" id=\"eqtl_add_button\" onclick=\"AddEQTLOverlay()\">Add eQTL</button>\n");
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");

		// variants of interest

		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">Variants of Interest</h3>\n");
		sb.append("<div class=\"akwrapper\">\n");
		sb.append("<table id='voi_table' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("  <thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false filter-false\">LD Summary</th>\n");		
		sb.append("    <th class=\"resizable-false\">Name</th>\n");
		sb.append("    <th class=\"resizable-false\">Non-Reference Allele</th>\n");
		sb.append("    <th class=\"resizable-false\">Location</th>\n");
		sb.append("    <th class=\"resizable-false\">Gene</th>\n");		
		sb.append("    <th class=\"resizable-false\">Protein Change</th>\n");
		sb.append("    <th class=\"resizable-false\">Polyphen</th>\n");
		sb.append("    <th class=\"resizable-false\">f(NFE)</th>\n");
		sb.append("    <th class=\"resizable-false\">f(Max)</th>\n");
		sb.append("  </tr>\n");
		sb.append("  </thead>\n");
		DecimalFormat formatter = new DecimalFormat("#,###");
		sb.append("<tbody>\n");
		for (int i=0; i<cvga.size(); ++i) {
			CodingVariantGeneAllele CV = cvga.get(i);
			sb.append("  <tr>\n");
			String ckd = "";
			if (CV.show_in_ld_groups) ckd = "checked";
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\"ldmarkers"+CV.name+"\" onclick=\"UpdateMarkerForLD(this)\" data-dbid=\""+CV.vm_id+"\" "+ckd+"></td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\"http://gnomad-old.broadinstitute.org/dbsnp/"+StringEscapeUtils.escapeHtml4(CV.name)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(CV.name)+"</a></td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(CV.allele)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(formatter.format(CV.start_1based))+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(CV.gene_symbol)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(CV.hgvs_protein)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(CV.polyphen)+"</td>\n");
			String f= "";
			if (CV.frequencies.containsKey("NFE")) f = CV.frequencies.get("NFE");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+f+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+CV.max_gnomadg_subpop_freq_str+"</td>\n");
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");		
		sb.append("</table>\n");
		sb.append("<div id=\"voi_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");

		// functional details
		
		sb.append(CreateDetailAccordion("functionalVariantsDetailsAccordion", "Functional Variants Details", "functionalDetailsDiv", "functionaldetailstable", allsourcedocuments, "Functional", "functional_details_pager"));
		
		// expression details
		
		sb.append(CreateDetailAccordion("expressionDetailsAccordion", "Expression Data Details", "expressionDetailsDiv", "expressiondetailstable", allsourcedocuments, "Expression", "expression_details_pager"));
		
		// protein details
		
		sb.append(CreateDetailAccordion("proteinDetailsAccordion", "Protein and Structure Chemistry Details", "proteinDetailsDiv", "proteindetailstable", allsourcedocuments, "Protein", "protein_details_pager"));
		
		// clinical results
		
		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">Clinical Results</h3>\n");
		sb.append("<div class=\"akwrapper\">\n");
		
		sb.append("<h3 style=\"text-align: center; font: 16px/24px Lato, Sans-serif;\"><b>Phenotype/Gene Associations</b></h3>\n");
		sb.append("<table id='genephen_table' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("  <thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">Phenotype</th>\n");		
		sb.append("    <th class=\"resizable-false\">Gene</th>\n");
		sb.append("    <th class=\"resizable-false\">Source</th>\n");
		sb.append("    <th class=\"resizable-false\">Curator Comment</th>\n");	
		sb.append("  </tr>\n");
		sb.append("  </thead>\n");
		sb.append("<tbody>\n");
		for (int i=0; i<genephenassocs.size(); ++i) {
			GenePhenAssoc gp = genephenassocs.get(i);
			sb.append("  <tr id=\'genephenrow"+gp.db_id+"\'>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(gp.phenotype)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(gp.gene)+"</td>\n");
			if (gp.linkout.equals("")) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(gp.source)+"</td>\n");
			} else {
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(gp.linkout)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(gp.source)+"</a></td>\n");
			}
			sb.append("    <td style=\"position:relative;\"><textarea class=\"ak_genephen_comment ak_tablecell\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+gp.db_id+"\" placeholder=\"Enter comment\" onkeyup=\"startgenephencommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(gp.curator_comment)+"</textarea></td>\n");
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");		
		sb.append("</table>\n");
		sb.append("<div id=\"genephen_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("</div>\n");		
		
		sb.append("<h3 style=\"text-align: center; font: 16px/24px Lato, Sans-serif;\"><b>Phenotype/Allele Associations</b></h3>\n");
		sb.append("<table id='phenallele_table' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("  <thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">Phenotype</th>\n");		
		sb.append("    <th class=\"resizable-false\">Variant</th>\n");
		sb.append("    <th class=\"resizable-false\">Unaudited<br>Risk Allele</th>\n");
		sb.append("    <th class=\"resizable-false\">Clinical Signficance</th>\n");
		sb.append("    <th class=\"resizable-false\">Transcript-Impact Genes</th>\n");
		sb.append("    <th class=\"resizable-false\">Source</th>\n");
		sb.append("  </tr>\n");
		sb.append("  </thead>\n");
		sb.append("<tbody>\n");
		for (int i=0; i<phenalleleassocs.size(); ++i) {
			PhenAlleleAssoc pa = phenalleleassocs.get(i);
			sb.append("  <tr>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.phenotype)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.phenotype)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.unaudited_risk_allele)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.clinical_significance)+"</td>\n");
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.gene_string)+"</td>\n");
			if (pa.linkout.equals("")) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(pa.source)+"</td>\n");
			} else {
			sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(pa.linkout)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(pa.source)+"</a></td>\n");
			}
			sb.append("  </tr>\n");
		}
		sb.append("</tbody>\n");		
		sb.append("</table>\n");

		sb.append("<div id=\"phenallele_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");
		
		// clinical details
		
		sb.append(CreateDetailAccordion("clinicalDetailsAccordion", "Clinical Data Details", "clinicalDetailsDiv", "clinicaldetailstable", allsourcedocuments, "Clinical", "clinical_details_pager"));

		// references
		
		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div id=\"referencesAccordion\" class=\"accordion\">\n");
		sb.append("<h3 id=\"referencesh3\" style=\"font: 12px/18px Lato, Sans-serif;\">References ("+unreviewed_reference_count+" out of "+total_reference_count+" not yet reviewed)</h3>\n");
		sb.append("<div class=\"akwrapper\" id='referencesDiv'>\n");
		sb.append("<table id='reftable' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("  <thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false filter-false\">Reviewed</th>\n");
		sb.append("    <th class=\"resizable-false\">1st Author</th>\n");
		sb.append("    <th class=\"resizable-false\">Year</th>\n");
		sb.append("    <th class=\"resizable-false\">Journal/Site/File</th>\n");		
		sb.append("    <th class=\"resizable-false\">Title/Description</th>\n");
		sb.append("    <th class=\"resizable-false\">Curator Comment</th>\n");	
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");
		sb.append("    <th class=\"resizable-false filter-false\"></th>\n");
		sb.append("  </tr>\n");
		sb.append("  </thead>\n");
		sb.append("<tbody>\n");
		for (int i=0; i<allsourcedocuments.size(); ++i) {
			SourceDocument sd = allsourcedocuments.get(i);
			int childcount=sd.details.size()+1;
			int rowspancount = childcount+1;
			sb.append("  <tr id=\'sourcedocumentsrow"+sd.id+"\'>\n");
			String rowclass = "akrefrowclass"+sd.id;
			
			String ckd = "";
			if (sd.has_been_reviewed==1) ckd = "checked";
			sb.append("    <td style=\"text-align:center\" ><input type=\"checkbox\" class=\""+rowclass+"\" onclick=\"UpdateSourceReviewed(this)\" data-dbid=\""+sd.id+"\" "+ckd+"></td>\n");
			if (sd.pmdoc!=null) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.first_author)+"</td>\n");				
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.pub_year)+"</td>\n");			
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.journal)+"</td>\n");			
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.title)+"</a></td>\n");
			} else if (sd.bdoc!=null) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.first_author)+"</td>\n");				
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.pub_year)+"</td>\n");			
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.publisher)+"</td>\n");			
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.title)+"</a></td>\n");					
			} else if (sd.fdoc!=null) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"></td>\n");				
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.year)+"</td>\n");			
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><div>"+StringEscapeUtils.escapeHtml4(sd.fdoc.name)+"<button style=\"margin-left:10px;\" class=\"ak_table_button "+rowclass+"\" onclick=\"DownloadFile('sourcedocumentsrow"+sd.id+"')\" >Download</button></div></td>\n");		
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.description)+"</td>\n");	
			} else if (sd.wdoc!=null) {
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"></td>\n");				
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.year)+"</td>\n");		
				//sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.site)+"</a></td>\n");
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.site.replaceAll("/", "/\u200B"))+"</a></td>\n");
				// put a zero-width space after each slash to facilitate word wrapping
				
				
				sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.description)+"</td>\n");	
			}
			sb.append("    <td style=\"position:relative;\" ><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"position:absolute; top:0; bottom:0; left:0; right:0; height:100%; width:100%; box-sizing:border-box;\" data-dbid=\""+sd.id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(sd.curator_comment)+"</textarea></td>\n");
			//sb.append("    <td><textarea class=\"ak_source_comment ak_tablecell "+rowclass+"\" style=\"width:173px; height:100%\" data-dbid=\""+sd.id+"\" placeholder=\"Enter comment\" onkeyup=\"startsourcecommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(sd.curator_comment)+"</textarea></td>\n");
			String xtra = "";
			if (sd.details.size()>0) xtra = " ("+sd.details.size()+")";
			sb.append("    <td style=\"vertical-align: middle;\"><button type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"detail-toggle ak_table_button "+rowclass+"\" >Show/Add Details"+xtra+"</button></td>");
			if (!sd.automatic) sb.append("    <td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"><button id=\"x_ref_"+sd.id+"\" type=\"button\" style=\"display: block; margin: auto; vertical-align: middle;\" class=\"ak_table_button "+rowclass+"\" onclick=\"RemoveRefRow('sourcedocumentsrow"+sd.id+"')\">X</button></td>");
			else sb.append("    <td rowspan=\""+rowspancount+"\" style=\"vertical-align: middle;\"></td>");	
			sb.append("  </tr>\n");
			for (int j=0; j<sd.details.size(); ++j) {
				Detail d = sd.details.get(j);
				String drowclass="akdetailrowclass"+d.id;
				sb.append("  <tr style=\"background: #eeeeee !important;\" id='detail"+d.id+"' class=\"tablesorter-childRow\" >\n");
				sb.append("    <td colspan=\"7\" style=\"vertical-align: middle\">\n");				
				sb.append("      <div style=\"width:1175px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">\n");
				sb.append("          <section style=\"width:150px; height:100px; display: inline-block; vertical-align: top; margin-top: 80px;\">");				
				sb.append("            <label style=\"display: block; font-size: 12px;\" >Section Assignment</label>");				
				sb.append("            <select style=\"text-align: left; display: block; width:140px;\" class=\"ak_detail_section_selector "+rowclass+" "+drowclass+"\" data-csection=\""+d.section_assignment+"\" onchange=\"UpdateSection(this)\" data-dbid=\""+d.id+"\">\n");
				if (d.section_assignment.equals("None")) sb.append("    <option selected=\"selected\" value=\"None\">None</option>\n");
				else sb.append("    <option value=\"None\">None</option>\n");
				if (d.section_assignment.equals("Association")) sb.append("    <option selected=\"selected\" value=\"Association\">Association</option>\n");
				else sb.append("    <option value=\"Association\">Association</option>\n");
				if (d.section_assignment.equals("Clinical")) sb.append("    <option selected=\"selected\" value=\"Clinical\">Clinical</option>\n");
				else sb.append("    <option value=\"Clinical\">Clinical</option>\n");
				if (d.section_assignment.equals("Expression")) sb.append("    <option selected=\"selected\" value=\"Expression\">Expression</option>\n");
				else sb.append("    <option value=\"Expression\">Expression</option>\n");
				if (d.section_assignment.equals("Functional")) sb.append("    <option selected=\"selected\" value=\"Functional\">Functional</option>\n");
				else sb.append("    <option value=\"Functional\">Functional</option>\n");
				if (d.section_assignment.equals("PQTL")) sb.append("    <option selected=\"selected\" value=\"PQTL\">PQTL</option>\n");
				else sb.append("    <option value=\"PQTL\">PQTL</option>\n");			
				if (d.section_assignment.equals("Protein")) sb.append("    <option selected=\"selected\" value=\"Protein\">Protein</option>\n");
				else sb.append("    <option value=\"Protein\">Protein</option>\n");
				sb.append("            </select>\n");
				sb.append("          </section>");		
				sb.append("          <textarea id='detailrow"+d.id+"' data-fromwhere=\"1\" data-dbid=\""+d.id+"\" class=\"ak_tablecell ak_det_desc "+rowclass+" "+drowclass+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 20px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(d.description)+"</textarea>\n");
				sb.append("          <a style=\"display: inline-block;\" class=\""+rowclass+" "+drowclass+"\" href=\""+d.b64_contents+"\" data-lightbox=\"detail"+d.id+"\">\n");
				sb.append("          <img class=\"contain_img "+rowclass+" "+drowclass+"\" src=\""+d.b64_contents+"\" width=\"300\" height=\"200\"></img></a>\n");
				sb.append("          <button id=\"x_detail_"+d.id+"\" type=\"button\" class=\"ak_table_button "+rowclass+" "+drowclass+"\" style=\"margin-left:50px;display: inline-block; vertical-align: top; margin-top:90px;\" onclick=\"RemoveDetail('detail"+d.id+"')\">Remove</button>\n");
				sb.append("      </div>\n");
				sb.append("    </td>\n");
				sb.append("  </tr>\n");				
			}
			sb.append("<tr style=\"background: #eeeeee !important; \" id=\'adddetail"+sd.id+"\' class=\"tablesorter-childRow\">");
			sb.append("<td colspan=\"7\" style=\"vertical-align: middle\"><div style=\"text-align: center;\"><button type=\"button\" class=\"ak_table_button "+rowclass+"\" style=\"margin-top:10px; margin-bottom:10px; margin-right:30px;display: inline;\" onclick=\"AddDetailOverlay('sourcedocumentsrow"+sd.id+"')\">Add Detail</button></div></td>");
			sb.append("  </tr>\n");			
		}
		sb.append("</tbody>\n");		
		sb.append("</table>\n");

		sb.append("<div id=\"ref_pager\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");
		sb.append("<button style=\"margin-left: 100px;\" class=\"ak_table_button\" id=\"ref_add_button\" onclick=\"AddPubmedOverlay()\">Add Pubmed Reference</button>\n");
		sb.append("<button style=\"margin-left: 100px;\" class=\"ak_table_button\" id=\"file_add_button\" onclick=\"AddFileOverlay()\">Add File Reference</button>\n");
		sb.append("<button style=\"margin-left: 100px;\" class=\"ak_table_button\" id=\"web_add_button\" onclick=\"AddWebOverlay()\">Add Web Reference</button>\n");
		sb.append("<button style=\"margin-left: 100px;\" class=\"ak_table_button\" id=\"biorxiv_add_button\" onclick=\"AddBiorxivOverlay()\">Add bioRxiv Reference</button>\n");       
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");		
		
		// supplemental methods etc.
		/*
		sb.append(AKJavascriptFxns.getJavascriptFunctions());
*/
		
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var hide_non_coding = "+hidenoncoding+";\n");
		sb.append("var SVGMode = '"+svg_mode+"';\n");
		
		sb.append("$.tablesorter.addParser({\n");
		sb.append("  id: 'akmanual',\n");
		sb.append("  is: function(s, table, cell, $cell) {\n");
		sb.append("    return false;\n");
		sb.append("  },\n");
		sb.append("  format: function(s, table, cell, cellIndex) {\n");
		sb.append("    return $(cell).find('button').length > 0 ? 0 : 1;\n");
		sb.append("  },\n");
		sb.append("  parsed: false,\n");
		sb.append("  type: 'numeric'\n");
		sb.append("});\n");
		
		sb.append("$( document ).ready(function() {\n");
		//sb.append("  var tgn_agree = getCookie(\"tgn_agree1\");\n");
		//sb.append("  if (tgn_agree==\"\") {\n");
		//sb.append("    $('.tgn_agree').show();");
		//sb.append("  }\n");
		sb.append("  $('#aksummaryarea').attr('data-curval', $( '#aksummaryarea' ).val());\n");	
		sb.append("  $('.ak_gwas_comment').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_det_desc').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_eqtl_comment').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_source_comment').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_gwas_sdn').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_eqtl_sdn').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-curval', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_mes_selector').each(function(i, obj) {\n");	
		sb.append("    $(this).attr('data-cmes', $(this).val());\n");
		sb.append("  });\n");
		sb.append("  $('.ak_mes_selector').select2({minimumResultsForSearch: 20});\n");
		sb.append("  $('.ak_detail_section_selector').select2({minimumResultsForSearch: 20});\n");
		sb.append("  $('#gwastable').tablesorter({theme:'blue', headers:{8:{sorter:'select'}, 9:{sorter:'checkbox'}, 11:{sorter:'akmanual'}}, sortList: [[11,1],[0,0]], widgets: ['filter', 'output', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, output_includeHeader:true, output_delivery:'d', output_saveRows:'a', output_separator:'\t', output_replaceQuote:'\u201c;', output_includeHTML:true, output_trimSpaces:true, output_wrapQuotes:true, output_saveFileName:'gwas_"+target_name+".xls', resizable_addLastColumn:false, resizable_widths: ['147px', '125px', '175px', '55px', '98px', '85px', '80px', '88px', '156px', '60px', '175px', '40px']}});\n");
		sb.append("  $('#gwastable').tablesorterPager({container: $(\"#gwas_pager\"),removeRows: false});\n");
		sb.append("  $('#gwastable th').css('text-align','center');\n");
		sb.append("  $('#gwastable th').css('padding','0px');\n");	
		
		sb.append("  $('#pqtltable').tablesorter({theme:'blue', headers:{8:{sorter:'select'}, 9:{sorter:'checkbox'}, 11:{sorter:'akmanual'}}, sortList: [[11,1],[0,0]], widgets: ['filter', 'output', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, output_includeHeader:true, output_delivery:'d', output_saveRows:'a', output_separator:'\t', output_replaceQuote:'\u201c;', output_includeHTML:true, output_trimSpaces:true, output_wrapQuotes:true, output_saveFileName:'gwas_"+target_name+".xls', resizable_addLastColumn:false, resizable_widths: ['147px', '125px', '175px', '55px', '98px', '85px', '80px', '88px', '156px', '60px', '175px', '40px']}});\n");
		sb.append("  $('#pqtltable').tablesorterPager({container: $(\"#pqtl_pager\"),removeRows: false});\n");
		sb.append("  $('#pqtltable th').css('text-align','center');\n");
		sb.append("  $('#pqtltable th').css('padding','0px');\n");	
		
		sb.append("  $('#eqtltable').tablesorter({theme:'blue', headers:{10:{sorter:'checkbox'}, 12:{sorter:'akmanual'}, 13:{sorter:'digit'}}, sortList: [[11,1],[0,0]], widgets: ['filter', 'output', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, output_includeHeader:true, output_delivery:'d', output_saveRows:'a', output_separator:'\t', output_replaceQuote:'\u201c;', output_includeHTML:true, output_trimSpaces:true, output_wrapQuotes:true, output_saveFileName:'eqtl_"+target_name+".xls', resizable_addLastColumn:false, resizable_widths: ['147px', '135px', '68px', '155px', '55px', '88px', '72px', '55px', '77px', '121px', '52px', '175px', '40px', '42px']}});\n");
		sb.append("  $('#eqtltable').tablesorterPager({container: $(\"#eqtl_pager\"),removeRows: false});\n");
		sb.append("  $('#eqtltable th').css('padding','0px');\n");	
		sb.append("  $('#eqtltable th').css('text-align','center');\n");
		sb.append("  $('.accordion').accordion({collapsible: true, active: false, heightStyle: \"content\"});\n");
		sb.append("  $('.ui-accordion').css('padding','0px');\n");
		sb.append("  $('.ui-accordion-content').css('padding','0px');\n");		
		String updown = "1";
		if (TargetAnnotation.is_forward_strand==1) updown = "0";
		sb.append("  $('#voi_table').tablesorter({theme:'blue', headers:{0:{sorter:'checkbox'}}, sortList: [[3,"+updown+"]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['140px','135px', '180px', '130px', '130px', '155px', '155px', '135px', '135px']} });\n");	
		sb.append("  $('#voi_table').tablesorterPager({container: $(\"#voi_pager\"),removeRows: false});\n");
		sb.append("  $('#voi_table th').css('text-align','center');\n");		
		sb.append("  $('#voi_table th').css('padding','0px');\n");
		
		sb.append("  $('#reftable .tablesorter-childRow td').hide();\n");		
		sb.append("  $('#reftable').tablesorter({theme:'blue', headers:{0:{sorter:'checkbox'}, 7:{sorter:'akmanual'}}, widgets: ['filter', 'output', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, output_includeHeader:true, output_delivery:'d', output_saveRows:'a', output_separator:'\t', output_replaceQuote:'\u201c;', output_includeHTML:true, output_trimSpaces:true, output_wrapQuotes:true, output_saveFileName:'refs_"+target_name+".xls', resizable_addLastColumn:false, resizable_widths: ['85px','145px','75px','200px','430px','175px','140px', '40px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		
		sb.append("  $('#reftable').tablesorterPager({container: $(\"#ref_pager\"),removeRows: false});\n");
		sb.append("  $('#reftable').delegate('.detail-toggle', 'click', function() {\n");
		sb.append("    if (master_busy) {\n");
		sb.append("      alert(\"Please re-try after background actions have completed.\");\n");
		sb.append("      return false;\n");
		sb.append("    }\n");
		sb.append("    master_busy = true;\n");
		sb.append("    $(this).text(function(i, v) {\n");
		sb.append("                                  var toret = v;\n");
		sb.append("                                  if (toret.indexOf('Show')!=-1) {toret = toret.replace('Show/Add', 'Hide');}\n");
		sb.append("                                  else {toret = toret.replace('Hide', 'Show/Add');}\n");
		sb.append("                                  return toret;\n");
		sb.append("                                 });\n");
		sb.append("    $(this).closest('tr').nextUntil('tr:not(.tablesorter-childRow)').each(function() {\n");
		sb.append("      $(this).find('textarea').each(function() {\n");
		sb.append("        $(this).val($(this).attr('data-curval'));\n");
		sb.append("      });\n");
		sb.append("    });\n");
		sb.append("    $(this).closest('tr').nextUntil('tr:not(.tablesorter-childRow)').find('td').toggle();\n");	
		sb.append("    master_busy = false;\n");
		sb.append("    return false;\n");		
		sb.append("  });\n");
		sb.append("  $('#reftable th').css('text-align','center');\n");
		sb.append("  $('#reftable th').css('padding','0px');\n");
		
		sb.append("  $('#clinicaldetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#clinicaldetailstable').tablesorterPager({container: $(\"#clinical_details_pager\"),removeRows: false});\n");
		sb.append("  $('#clinicaldetailstable th').css('text-align','center');\n");
		sb.append("  $('#clinicaldetailstable th').css('padding','0px');\n");
		sb.append("  $('#clinicaldetailstable tr').show();\n"); // not sure why this is needed by default?
		sb.append("  $('#expressiondetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#expressiondetailstable').tablesorterPager({container: $(\"#expression_details_pager\"),removeRows: false});\n");
		sb.append("  $('#expressiondetailstable th').css('text-align','center');\n");
		sb.append("  $('#expressiondetailstable th').css('padding','0px');\n");
		sb.append("  $('#expressiondetailstable tr').show();\n"); // not sure why this is needed by default?
		sb.append("  $('#functionaldetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#functionaldetailstable').tablesorterPager({container: $(\"#functional_details_pager\"),removeRows: false});\n");
		sb.append("  $('#functionaldetailstable th').css('text-align','center');\n");
		sb.append("  $('#functionaldetailstable th').css('padding','0px');\n");
		sb.append("  $('#functionaldetailstable tr').show();\n"); // not sure why this is needed by default?
		sb.append("  $('#proteindetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#proteindetailstable').tablesorterPager({container: $(\"#protein_details_pager\"),removeRows: false});\n");
		sb.append("  $('#proteindetailstable th').css('text-align','center');\n");
		sb.append("  $('#proteindetailstable th').css('padding','0px');\n");
		sb.append("  $('#proteindetailstable tr').show();\n"); // not sure why this is needed by default?
		
		
		sb.append("  $('#associationdetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#associationdetailstable').tablesorterPager({container: $(\"#association_details_pager\"),removeRows: false});\n");
		sb.append("  $('#associationdetailstable th').css('text-align','center');\n");
		sb.append("  $('#associationdetailstable th').css('padding','0px');\n");
		sb.append("  $('#associationdetailstable tr').show();\n"); // not sure why this is needed by default?	
		
		sb.append("  $('#pqtldetailstable').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['232px','82px','326px','651px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#pqtldetailstable').tablesorterPager({container: $(\"#pqtl_details_pager\"),removeRows: false});\n");
		sb.append("  $('#pqtldetailstable th').css('text-align','center');\n");
		sb.append("  $('#pqtldetailstable th').css('padding','0px');\n");
		sb.append("  $('#pqtldetailstable tr').show();\n"); // not sure why this is needed by default?	
		
		
		sb.append("  $('#genephen_table').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['290px','75px','250px', '680px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#genephen_table').tablesorterPager({container: $(\"#genephen_pager\"),removeRows: false});\n");
		sb.append("  $('#genephen_table th').css('text-align','center');\n");
		sb.append("  $('#genephen_table th').css('padding','0px');\n");
		sb.append("  $('#phenallele_table').tablesorter({theme:'blue', sortList: [[0,1],[1,0]], widgets: ['filter', 'resizable'], widgetOptions :{filter_columnFilters:true, filter_hideFilters:true, resizable_addLastColumn:false, resizable_widths: ['375px','200px','120px', '240px', '242px', '120px'] }, cssChildRow: \"tablesorter-childRow\"});\n");
		sb.append("  $('#phenallele_table').tablesorterPager({container: $(\"#phenallele_pager\"),removeRows: false});\n");
		sb.append("  $('#phenallele_table th').css('text-align','center');\n");
		sb.append("  $('#phenallele_table th').css('padding','0px');\n");
		sb.append("});\n");
		sb.append("</script>\n");
		sb.append("</div>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}	
    /*
	public static String GetContact() throws Exception {
		Vector<String> v = GetHeadAndTailSections("Contact");
		StringBuilder sb = new StringBuilder();
		sb.append(v.get(0));
		sb.append("<div>\n");
		sb.append("<h1>Contact</h1>\n");
		sb.append("<p>This software is provided as a beta release to encourage collaboration in the community on Target Gene Notebook as a non-proprietary resource for efficient linking of genetic associations to functional biological information. This process is essential to translating genetic insights into therapeutic hypotheses and, eventually, drug discovery.</p>\n");
		sb.append("<p>If you have questions, comments, would like to request source code or collaborate on this resource please send an <a href=\"mailto:targetgenenotebook@eisai.com\">email</a>.</p>\n");
		sb.append("</div>\n");
		sb.append(v.get(1));
		return sb.toString();
	}
    */
    /*
	public static String GetTerms() throws Exception {
		Vector<String> v = GetHeadAndTailSections("Terms");
		StringBuilder sb = new StringBuilder();
		sb.append(v.get(0));
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("public/content/agreement.txt");
		String agree_text = IOUtils.toString(is);
		is.close();	
		sb.append("<div>\n");
		sb.append("<h1>Terms</h1>\n");
		String[] lines = agree_text.split("\\r?\\n");
        for (String line : lines) {
        	if (line.equals("")) sb.append("<br>\n");
        	else sb.append("<p>"+StringEscapeUtils.escapeHtml4(line)+"</p>\n");
        }			
		sb.append("</div>\n");
		sb.append(v.get(1));
		return sb.toString();
	}
    */
    /*
	public static String GetAbout() throws Exception {
		Vector<String> v = GetHeadAndTailSections("About");
		StringBuilder sb = new StringBuilder();
		sb.append(v.get(0));
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("public/content/about.txt");
		String about_text = IOUtils.toString(is);
		is.close();	
		sb.append("<div>\n");
		sb.append("<h1>About</h1>\n");
		String[] lines = about_text.split("\\r?\\n");
        for (String line : lines) {
        	if (line.equals("")) sb.append("<br>\n");
        	else sb.append("<p>"+StringEscapeUtils.escapeHtml4(line)+"</p>\n");
        }			
		sb.append("</div>\n");
		sb.append(v.get(1));
		return sb.toString();
	}
    */
    /*
	public static String GetFAQ() throws Exception {
		Vector<String> v = GetHeadAndTailSections("FAQ");
		StringBuilder sb = new StringBuilder();
		sb.append(v.get(0));
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("public/content/faq.txt");
		String faq_text = IOUtils.toString(is);
		is.close();	
		sb.append("<div>\n");
		sb.append("<h1>FAQ</h1>\n");
		String[] lines = faq_text.split("\\r?\\n");
        for (String line : lines) {
        	if (line.equals("General Use")) sb.append("<h2>"+line+"</h2>\n");
        	else if (line.equals("Technical Details")) sb.append("<h2>"+line+"</h2>\n");
        	else if (line.endsWith("?")) {
        		sb.append("<div>\n");
        		sb.append("<div class=\"accordion\">\n");
        		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">"+StringEscapeUtils.escapeHtml4(line)+"</h3>\n");
        		sb.append("<div>\n");
        		sb.append("<div style=\"width:100%\">\n");
        	} else if (line.equals("")) {
        		sb.append("</div>\n");
        		sb.append("</div>\n");
        		sb.append("</div>\n");
        		sb.append("</div>\n");
        	} else sb.append("<p>"+StringEscapeUtils.escapeHtml4(line)+"</p>\n");
        }			
		sb.append("</div>\n");
		sb.append(v.get(1));
		return sb.toString();
	}
    */	
	public static Vector<String> GetHeadAndTailSections(String section_name) throws Exception {
		Vector<String> toret = new Vector<>();
		StringBuilder sb = new StringBuilder();
		sb.append(GetHTMLStart("TGN - "+section_name,"",""));
		toret.add(sb.toString());
		StringBuilder sb2 = new StringBuilder();
		sb2.append("<script type=\"text/javascript\">\n");
		sb2.append("$( document ).ready(function() {\n");
		sb2.append("  var tgn_agree = getCookie(\"tgn_agree1\");\n");
		sb2.append("  if (tgn_agree==\"\") {\n");
		sb2.append("    $('.tgn_agree').show();");
		sb2.append("  }\n");		
		sb2.append("  $('.accordion').accordion({collapsible: true, active: false, heightStyle: \"content\"});\n");
		sb2.append("  $('.ui-accordion').css('padding','0px');\n");
		sb2.append("  $('.ui-accordion-content').css('padding','0px');\n");	
		sb2.append("});\n");
		sb2.append("</script>\n");
		sb2.append("</body>\n");
		sb2.append("</html>\n");
		toret.add(sb2.toString());
		return toret;
	}
		
	public static String CreateDetailAccordion(String accordion_id, String h3_name, String div_id, String table_id, Vector<SourceDocument> allsourcedocs, String section_name, String pager_id  ) {
		StringBuffer sb = new StringBuffer();
		String sect_lc = section_name.toLowerCase();
		sb.append("<div style=\"width:1300px; margin: 0 auto;\">\n");
		sb.append("<div id=\""+accordion_id+"\" class=\"accordion\">\n");
		sb.append("<h3 style=\"font: 12px/18px Lato, Sans-serif;\">"+h3_name+"</h3>\n");
		sb.append("<div class=\"akwrapper\" id='"+div_id+"'>\n");
		sb.append("<table id='"+table_id+"' class=\"display\" cellspacing=\"0\">\n");		
		sb.append("  <thead>\n");
		sb.append("  <tr>\n");
		sb.append("    <th class=\"resizable-false\">1st Author</th>\n");
		sb.append("    <th class=\"resizable-false\">Year</th>\n");
		sb.append("    <th class=\"resizable-false\">Journal/Site/File</th>\n");		
		sb.append("    <th class=\"resizable-false\">Title/Description</th>\n");
		sb.append("  </tr>\n");
		sb.append("  </thead>\n");
		sb.append("<tbody>\n");
		for (int i=0; i<allsourcedocs.size(); ++i) {
			SourceDocument sd = allsourcedocs.get(i);
			int count = 0;
			for (int j=0; j<sd.details.size(); ++j) {
				if (sd.details.get(j).section_assignment.equals(section_name)) count++;
			}
			if (count>0) {
				sb.append("  <tr id=\'"+sect_lc+"_sourcedocumentsrow"+sd.id+"\'>\n");
				if (sd.pmdoc!=null) {
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.first_author)+"</td>\n");				
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.pub_year)+"</td>\n");			
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.journal)+"</td>\n");			
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.pmdoc.title)+"</a></td>\n");
				} else if (sd.bdoc!=null) {
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.first_author)+"</td>\n");				
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.pub_year)+"</td>\n");			
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.publisher)+"</td>\n");			
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.bdoc.title)+"</a></td>\n");									
				} else if (sd.fdoc!=null) {
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"></td>\n");				
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.year)+"</td>\n");			
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.name)+"</td>\n");		
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.fdoc.description)+"</td>\n");
				} else if (sd.wdoc!=null) {
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"></td>\n");				
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.year)+"</td>\n");		
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\"><a href=\""+StringEscapeUtils.escapeHtml4(sd.outbound_link)+"\" target=\"_blank\">"+StringEscapeUtils.escapeHtml4(sd.outbound_link.replaceAll("/", "/\u200B"))+"</a></td>\n");
					sb.append("    <td style=\"text-align: center;\" class=\"ak_tablecell\">"+StringEscapeUtils.escapeHtml4(sd.wdoc.description)+"</td>\n");
				}
				sb.append("  </tr>\n");
				for (int j=0; j<sd.details.size(); ++j) {
					Detail d = sd.details.get(j);
					if (d.section_assignment.equals(section_name)) {
						String rowclass = "akdetailrowclass"+d.id;
						sb.append("  <tr style=\"background: #eeeeee !important;\" id='"+sect_lc+"_detail"+d.id+"' class=\"tablesorter-childRow\" >\n");
						sb.append("    <td colspan=\"4\" style=\"vertical-align: middle\">\n");
						sb.append("      <div style=\"width:1000px; height:200px; margin-top:10px; margin-bottom:10px; margin-left: auto; margin-right: auto; position: relative;\">\n");
						sb.append("        <textarea class=\"ak_tablecell ak_det_desc "+rowclass+"\" id='detailrowsection"+d.id+"' data-fromwhere=\"2\" data-dbid=\""+d.id+"\" style=\"width:600px; height:180px; margin-top: 10px !important; margin-right: 70px !important; display: inline-block; vertical-align: top;\" placeholder=\"Enter description\" onkeyup=\"startdetailcommenttimer(this)\">"+StringEscapeUtils.escapeHtml4(d.description)+"</textarea>\n");
						sb.append("        <a style=\"display: inline-block;\" class=\""+rowclass+"\" href=\""+d.b64_contents+"\" data-lightbox=\""+sect_lc+"_detail"+d.id+"\">\n");
						sb.append("        <img class=\"contain_img "+rowclass+"\" src=\""+d.b64_contents+"\" width=\"300\" height=\"200\"></img></a>\n");
						sb.append("  </div></td></tr>\n");
					}
				}
			}
		}
		sb.append("</tbody>\n");		
		sb.append("</table>\n");
		sb.append("<div id=\""+pager_id+"\" style=\"text-align: left; margin-left: 5px;\" class=\"pager tablesorter-pager\">\n");
		sb.append("		  \n");
		sb.append("        Page: \n");
		sb.append("<select class=\"gotoPage\"></select>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/first.png\" class=\"first\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/prev.png\" class=\"prev\"/>\n");
		sb.append("<span class=\"pagedisplay\"></span>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/next.png\" class=\"next\"/>\n");
		sb.append("<img src=\"tablesorter-master/addons/pager/icons/last.png\" class=\"last\"/>\n");
		sb.append("<select class=\"pagesize\">\n");
		sb.append("<option selected=\"selected\"  value=\"10\">10</option>\n");
		sb.append("<option value=\"20\">20</option>\n");
		sb.append("<option value=\"30\">30</option>\n");
		sb.append("<option value=\"40\">40</option>\n");
		sb.append("</select>\n");     
		sb.append("</div>\n");		
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</div>\n");		
		return sb.toString();
	}

}
