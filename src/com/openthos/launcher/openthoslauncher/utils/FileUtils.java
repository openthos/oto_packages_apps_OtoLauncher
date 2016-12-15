package com.openthos.launcher.openthoslauncher.utils;

import java.io.File;
import com.android.launcher3.R;

/**
 * Created by xu on 2016/8/22.
 */
public class FileUtils {

    public static String getMIMEType(File file) {
        String type = "*/*";

        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("")) return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

   private static final String SUFFIX_APE = ".ape";
   private static final String SUFFIX_AVI = ".avi";
   private static final String SUFFIX_DOC = ".doc";
   private static final String SUFFIX_HTML = ".html";
   private static final String SUFFIX_MP3 = ".mp3";
   private static final String SUFFIX_MP4 = ".mp4";
   private static final String SUFFIX_PPT = ".ppt";
   private static final String SUFFIX_TXT = ".txt";
   private static final String SUFFIX_WAV = ".wav";
   private static final String SUFFIX_WMV = ".wmv";
   private static final String SUFFIX_XLS = ".xls";
   private static final String SUFFIX_PDF = ".pdf";
   private static final String SUFFIX_RM = ".rm";
   private static final String SUFFIX_RMVB = ".rmvb";
   private static final String SUFFIX_TAR = ".tar";
   private static final String SUFFIX_BZ2 = ".bz2";
   private static final String SUFFIX_GZ = ".gz";
   private static final String SUFFIX_ZIP = ".zip";
   private static final String SUFFIX_RAR = ".rar";

   public static int getFileIcon(String path) {
       if (!path.contains(".")) {
           return R.drawable.suffix_default;
       }
       String suffix = path.substring(path.lastIndexOf("."), path.length()).toLowerCase();
       switch (suffix) {
           case SUFFIX_APE:
               return R.drawable.suffix_ape;
           case SUFFIX_AVI:
               return R.drawable.suffix_avi;
           case SUFFIX_DOC:
               return R.drawable.suffix_doc;
           case SUFFIX_HTML:
               return R.drawable.suffix_html;
           case SUFFIX_MP3:
               return R.drawable.suffix_mp3;
           case SUFFIX_MP4:
               return R.drawable.suffix_mp4;
           case SUFFIX_PPT:
               return R.drawable.suffix_ppt;
           case SUFFIX_TXT:
               return R.drawable.suffix_txt;
           case SUFFIX_WAV:
               return R.drawable.suffix_wav;
           case SUFFIX_WMV:
               return R.drawable.suffix_wmv;
           case SUFFIX_XLS:
               return R.drawable.suffix_xls;
           case SUFFIX_PDF:
               return R.drawable.suffix_default;
           case SUFFIX_RM:
               return R.drawable.suffix_default;
           case SUFFIX_RMVB:
               return R.drawable.suffix_default;
           case SUFFIX_TAR:
               return R.drawable.suffix_default;
           case SUFFIX_BZ2:
               return R.drawable.suffix_default;
           case SUFFIX_GZ:
               return R.drawable.suffix_default;
           case SUFFIX_ZIP:
               return R.drawable.suffix_default;
           case SUFFIX_RAR:
               return R.drawable.suffix_default;
           default:
               return R.drawable.suffix_default;
       }
   }

    /**
     * getFileIcon: Get the Icon from the file name.
     *
     * @param file Thefile.
     * @return icon the ICON Type of the file.
     * @throws
     */
    public static String getFileIcon(File file) {
        String icon_folder = "icon_folder";
        String icon_file = "icon_file";

        if (file.isDirectory()) {
            return icon_folder;
        } else if (file.isFile()) {
            String name = file.getName();
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex < 0) {
                return icon_file;
            }

            String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
            if (end.equals("")) return icon_file;
            if (end.equals("apk")) return null;
            for (int i = 0; i < ICON_MapTable.length; i++) {
                if (end.equals(ICON_MapTable[i][0]))
                    icon_file = ICON_MapTable[i][1];
            }
            return icon_file;
        } else {
            return icon_file;
        }

    }

    /**
     * The Table of MIME Types
     */
    public static final String[][] MIME_MapTable = {
            {"3gp", "video/3gpp"},
            {"aab", "application/x-authoware-bin"},
            {"aam", "application/x-authoware-map"},
            {"aas", "application/x-authoware-seg"},
            {"ai", "application/postscript"},
            {"aif", "audio/x-aiff"},
            {"aifc", "audio/x-aiff"},
            {"aiff", "audio/x-aiff"},
            {"als", "audio/X-Alpha5"},
            {"amc", "application/x-mpeg"},
            {"ani", "application/octet-stream"},
            {"apk", "application/vnd.android.package-archive"},
            {"asc", "text/plain"},
            {"asd", "application/astound"},
            {"asf", "video/x-ms-asf"},
            {"asn", "application/astound"},
            {"asp", "application/x-asap"},
            {"asx", "video/x-ms-asf"},
            {"au", "audio/basic"},
            {"avb", "application/octet-stream"},
            {"avi", "video/x-msvideo"},
            {"awb", "audio/amr-wb"},
            {"bcpio", "application/x-bcpio"},
            {"bin", "application/octet-stream"},
            {"bld", "application/bld"},
            {"bld2", "application/bld2"},
            {"bmp", "image/bmp"},
            {"bpk", "application/octet-stream"},
            {"bz2", "application/x-bzip2"},
            {"c", "text/x-csrc"},
            {"cpp", "text/x-c++src"},
            {"cal", "image/x-cals"},
            {"ccn", "application/x-cnc"},
            {"cco", "application/x-cocoa"},
            {"cdf", "application/x-netcdf"},
            {"cgi", "magnus-internal/cgi"},
            {"chat", "application/x-chat"},
            {"class", "application/octet-stream"},
            {"clp", "application/x-msclip"},
            {"cmx", "application/x-cmx"},
            {"co", "application/x-cult3d-object"},
            {"cod", "image/cis-cod"},
            {"cpio", "application/x-cpio"},
            {"cpt", "application/mac-compactpro"},
            {"crd", "application/x-mscardfile"},
            {"csh", "application/x-csh"},
            {"csm", "chemical/x-csml"},
            {"csml", "chemical/x-csml"},
            {"css", "text/css"},
            {"cur", "application/octet-stream"},
            {"dcm", "x-lml/x-evm"},
            {"dcr", "application/x-director"},
            {"dcx", "image/x-dcx"},
            {"dhtml", "text/html"},
            {"dir", "application/x-director"},
            {"dll", "application/octet-stream"},
            {"dmg", "application/octet-stream"},
            {"dms", "application/octet-stream"},
            {"doc", "application/msword"},
            {"dot", "application/x-dot"},
            {"dvi", "application/x-dvi"},
            {"dwf", "drawing/x-dwf"},
            {"dwg", "application/x-autocad"},
            {"dxf", "application/x-autocad"},
            {"dxr", "application/x-director"},
            {"ebk", "application/x-expandedbook"},
            {"emb", "chemical/x-embl-dl-nucleotide"},
            {"embl", "chemical/x-embl-dl-nucleotide"},
            {"eps", "application/postscript"},
            {"eri", "image/x-eri"},
            {"es", "audio/echospeech"},
            {"esl", "audio/echospeech"},
            {"etc", "application/x-earthtime"},
            {"etx", "text/x-setext"},
            {"evm", "x-lml/x-evm"},
            {"evy", "application/x-envoy"},
            {"exe", "application/octet-stream"},
            {"fh4", "image/x-freehand"},
            {"fh5", "image/x-freehand"},
            {"fhc", "image/x-freehand"},
            {"fif", "image/fif"},
            {"fm", "application/x-maker"},
            {"fpx", "image/x-fpx"},
            {"fvi", "video/isivideo"},
            {"gau", "chemical/x-gaussian-input"},
            {"gca", "application/x-gca-compressed"},
            {"gdb", "x-lml/x-gdb"},
            {"gif", "image/gif"},
            {"gps", "application/x-gps"},
            {"gtar", "application/x-gtar"},
            {"gz", "application/x-gzip"},
            {"h", "text/x-chdr"},
            {"hdf", "application/x-hdf"},
            {"hdm", "text/x-hdml"},
            {"hdml", "text/x-hdml"},
            {"hlp", "application/winhlp"},
            {"hqx", "application/mac-binhex40"},
            {"htm", "text/html"},
            {"html", "text/html"},
            {"hts", "text/html"},
            {"ice", "x-conference/x-cooltalk"},
            {"ico", "application/octet-stream"},
            {"ief", "image/ief"},
            {"ifm", "image/gif"},
            {"ifs", "image/ifs"},
            {"imy", "audio/melody"},
            {"ins", "application/x-NET-Install"},
            {"ips", "application/x-ipscript"},
            {"ipx", "application/x-ipix"},
            {"it", "audio/x-mod"},
            {"itz", "audio/x-mod"},
            {"ivr", "i-world/i-vrml"},
            {"j2k", "image/j2k"},
            {"jad", "text/vnd.sun.j2me.app-descriptor"},
            {"jam", "application/x-jam"},
            {"java", "application/x-java"},
            {"jar", "application/java-archive"},
            {"jnlp", "application/x-java-jnlp-file"},
            {"jpe", "image/jpeg"},
            {"jpeg", "image/jpeg"},
            {"jpg", "image/jpeg"},
            {"jpz", "image/jpeg"},
            {"js", "application/x-javascript"},
            {"jwc", "application/jwc"},
            {"kjx", "application/x-kjx"},
            {"lak", "x-lml/x-lak"},
            {"latex", "application/x-latex"},
            {"lcc", "application/fastman"},
            {"lcl", "application/x-digitalloca"},
            {"lcr", "application/x-digitalloca"},
            {"lgh", "application/lgh"},
            {"lha", "application/octet-stream"},
            {"lml", "x-lml/x-lml"},
            {"lmlpack", "x-lml/x-lmlpack"},
            {"lsf", "video/x-ms-asf"},
            {"lsx", "video/x-ms-asf"},
            {"lzh", "application/x-lzh"},
            {"m13", "application/x-msmediaview"},
            {"m14", "application/x-msmediaview"},
            {"m15", "audio/x-mod"},
            {"m3u", "audio/x-mpegurl"},
            {"m3url", "audio/x-mpegurl"},
            {"ma1", "audio/ma1"},
            {"ma2", "audio/ma2"},
            {"ma3", "audio/ma3"},
            {"ma5", "audio/ma5"},
            {"man", "application/x-troff-man"},
            {"map", "magnus-internal/imagemap"},
            {"mbd", "application/mbedlet"},
            {"mct", "application/x-mascot"},
            {"mdb", "application/x-msaccess"},
            {"mdz", "audio/x-mod"},
            {"me", "application/x-troff-me"},
            {"mel", "text/x-vmel"},
            {"mi", "application/x-mif"},
            {"mid", "audio/midi"},
            {"midi", "audio/midi"},
            {"mif", "application/x-mif"},
            {"mil", "image/x-cals"},
            {"mio", "audio/x-mio"},
            {"mmf", "application/x-skt-lbs"},
            {"mng", "video/x-mng"},
            {"mny", "application/x-msmoney"},
            {"moc", "application/x-mocha"},
            {"mocha", "application/x-mocha"},
            {"mod", "audio/x-mod"},
            {"mof", "application/x-yumekara"},
            {"mol", "chemical/x-mdl-molfile"},
            {"mop", "chemical/x-mopac-input"},
            {"mov", "video/quicktime"},
            {"movie", "video/x-sgi-movie"},
            {"mp2", "audio/x-mpeg"},
            {"mp3", "audio/x-mpeg"},
            {"mp4", "video/mp4"},
            {"mpc", "application/vnd.mpohun.certificate"},
            {"mpe", "video/mpeg"},
            {"mpeg", "video/mpeg"},
            {"mpg", "video/mpeg"},
            {"mpg4", "video/mp4"},
            {"mpga", "audio/mpeg"},
            {"mpn", "application/vnd.mophun.application"},
            {"mpp", "application/vnd.ms-project"},
            {"mps", "application/x-mapserver"},
            {"mrl", "text/x-mrml"},
            {"mrm", "application/x-mrm"},
            {"ms", "application/x-troff-ms"},
            {"mts", "application/metastream"},
            {"mtx", "application/metastream"},
            {"mtz", "application/metastream"},
            {"mzv", "application/metastream"},
            {"nar", "application/zip"},
            {"nbmp", "image/nbmp"},
            {"nc", "application/x-netcdf"},
            {"ndb", "x-lml/x-ndb"},
            {"ndwn", "application/ndwn"},
            {"nif", "application/x-nif"},
            {"nmz", "application/x-scream"},
            {"nokia-op-logo", "image/vnd.nok-oplogo-color"},
            {"npx", "application/x-netfpx"},
            {"nsnd", "audio/nsnd"},
            {"nva", "application/x-neva1"},
            {"oda", "application/oda"},
            {"oom", "application/x-AtlasMate-Plugin"},
            {"pac", "audio/x-pac"},
            {"pae", "audio/x-epac"},
            {"pan", "application/x-pan"},
            {"pbm", "image/x-portable-bitmap"},
            {"pcx", "image/x-pcx"},
            {"pda", "image/x-pda"},
            {"pdb", "chemical/x-pdb"},
            {"pdf", "application/pdf"},
            {"pfr", "application/font-tdpfr"},
            {"pgm", "image/x-portable-graymap"},
            {"pict", "image/x-pict"},
            {"pm", "application/x-perl"},
            {"pmd", "application/x-pmd"},
            {"png", "image/png"},
            {"pnm", "image/x-portable-anymap"},
            {"pnz", "image/png"},
            {"pot", "application/vnd.ms-powerpoint"},
            {"ppm", "image/x-portable-pixmap"},
            {"pps", "application/vnd.ms-powerpoint"},
            {"ppt", "application/vnd.ms-powerpoint"},
            {"pqf", "application/x-cprplayer"},
            {"pqi", "application/cprplayer"},
            {"prc", "application/x-prc"},
            {"proxy", "application/x-ns-proxy-autoconfig"},
            {"ps", "application/postscript"},
            {"ptlk", "application/listenup"},
            {"pub", "application/x-mspublisher"},
            {"pvx", "video/x-pv-pvx"},
            {"qcp", "audio/vnd.qcelp"},
            {"qt", "video/quicktime"},
            {"qti", "image/x-quicktime"},
            {"qtif", "image/x-quicktime"},
            {"r3t", "text/vnd.rn-realtext3d"},
            {"ra", "audio/x-pn-realaudio"},
            {"ram", "audio/x-pn-realaudio"},
            {"rar", "application/x-rar-compressed"},
            {"ras", "image/x-cmu-raster"},
            {"rdf", "application/rdf+xml"},
            {"rf", "image/vnd.rn-realflash"},
            {"rgb", "image/x-rgb"},
            {"rlf", "application/x-richlink"},
            {"rm", "audio/x-pn-realaudio"},
            {"rmf", "audio/x-rmf"},
            {"rmm", "audio/x-pn-realaudio"},
            {"rmvb", "audio/x-pn-realaudio"},
            {"rnx", "application/vnd.rn-realplayer"},
            {"roff", "application/x-troff"},
            {"rp", "image/vnd.rn-realpix"},
            {"rpm", "audio/x-pn-realaudio-plugin"},
            {"rt", "text/vnd.rn-realtext"},
            {"rte", "x-lml/x-gps"},
            {"rtf", "application/rtf"},
            {"rtg", "application/metastream"},
            {"rtx", "text/richtext"},
            {"rv", "video/vnd.rn-realvideo"},
            {"rwc", "application/x-rogerwilco"},
            {"s3m", "audio/x-mod"},
            {"s3z", "audio/x-mod"},
            {"sca", "application/x-supercard"},
            {"scd", "application/x-msschedule"},
            {"sdf", "application/e-score"},
            {"sea", "application/x-stuffit"},
            {"sgm", "text/x-sgml"},
            {"sgml", "text/x-sgml"},
            {"sh", "application/x-sh"},
            {"shar", "application/x-shar"},
            {"shtml", "magnus-internal/parsed-html"},
            {"shw", "application/presentations"},
            {"si6", "image/si6"},
            {"si7", "image/vnd.stiwap.sis"},
            {"si9", "image/vnd.lgtwap.sis"},
            {"sis", "application/vnd.symbian.install"},
            {"sit", "application/x-stuffit"},
            {"skd", "application/x-Koan"},
            {"skm", "application/x-Koan"},
            {"skp", "application/x-Koan"},
            {"skt", "application/x-Koan"},
            {"slc", "application/x-salsa"},
            {"smd", "audio/x-smd"},
            {"smi", "application/smil"},
            {"smil", "application/smil"},
            {"smp", "application/studiom"},
            {"smz", "audio/x-smd"},
            {"snd", "audio/basic"},
            {"spc", "text/x-speech"},
            {"spl", "application/futuresplash"},
            {"spr", "application/x-sprite"},
            {"sprite", "application/x-sprite"},
            {"spt", "application/x-spt"},
            {"src", "application/x-wais-source"},
            {"stk", "application/hyperstudio"},
            {"stm", "audio/x-mod"},
            {"sv4cpio", "application/x-sv4cpio"},
            {"sv4crc", "application/x-sv4crc"},
            {"svf", "image/vnd"},
            {"svg", "image/svg-xml"},
            {"svh", "image/svh"},
            {"svr", "x-world/x-svr"},
            {"swf", "application/x-shockwave-flash"},
            {"swfl", "application/x-shockwave-flash"},
            {"t", "application/x-troff"},
            {"tad", "application/octet-stream"},
            {"talk", "text/x-speech"},
            {"tar", "application/x-tar"},
            {"taz", "application/x-tar"},
            {"tbp", "application/x-timbuktu"},
            {"tbt", "application/x-timbuktu"},
            {"tcl", "application/x-tcl"},
            {"tex", "application/x-tex"},
            {"texi", "application/x-texinfo"},
            {"texinfo", "application/x-texinfo"},
            {"tgz", "application/x-tar"},
            {"thm", "application/vnd.eri.thm"},
            {"tif", "image/tiff"},
            {"tiff", "image/tiff"},
            {"tki", "application/x-tkined"},
            {"tkined", "application/x-tkined"},
            {"toc", "application/toc"},
            {"toy", "image/toy"},
            {"tr", "application/x-troff"},
            {"trk", "x-lml/x-gps"},
            {"trm", "application/x-msterminal"},
            {"tsi", "audio/tsplayer"},
            {"tsp", "application/dsptype"},
            {"tsv", "text/tab-separated-values"},
            {"tsv", "text/tab-separated-values"},
            {"ttf", "application/octet-stream"},
            {"ttz", "application/t-time"},
            {"txt", "text/plain"},
            {"ult", "audio/x-mod"},
            {"ustar", "application/x-ustar"},
            {"uu", "application/x-uuencode"},
            {"uue", "application/x-uuencode"},
            {"vcd", "application/x-cdlink"},
            {"vcf", "text/x-vcard"},
            {"vdo", "video/vdo"},
            {"vib", "audio/vib"},
            {"viv", "video/vivo"},
            {"vivo", "video/vivo"},
            {"vmd", "application/vocaltec-media-desc"},
            {"vmf", "application/vocaltec-media-file"},
            {"vmi", "application/x-dreamcast-vms-info"},
            {"vms", "application/x-dreamcast-vms"},
            {"vox", "audio/voxware"},
            {"vqe", "audio/x-twinvq-plugin"},
            {"vqf", "audio/x-twinvq"},
            {"vql", "audio/x-twinvq"},
            {"vre", "x-world/x-vream"},
            {"vrml", "x-world/x-vrml"},
            {"vrt", "x-world/x-vrt"},
            {"vrw", "x-world/x-vream"},
            {"vts", "workbook/formulaone"},
            {"wav", "audio/x-wav"},
            {"wax", "audio/x-ms-wax"},
            {"wbmp", "image/vnd.wap.wbmp"},
            {"web", "application/vnd.xara"},
            {"wi", "image/wavelet"},
            {"wis", "application/x-InstallShield"},
            {"wm", "video/x-ms-wm"},
            {"wma", "audio/x-ms-wma"},
            {"wmd", "application/x-ms-wmd"},
            {"wmf", "application/x-msmetafile"},
            {"wml", "text/vnd.wap.wml"},
            {"wmlc", "application/vnd.wap.wmlc"},
            {"wmls", "text/vnd.wap.wmlscript"},
            {"wmlsc", "application/vnd.wap.wmlscriptc"},
            {"wmlscript", "text/vnd.wap.wmlscript"},
            {"wmv", "audio/x-ms-wmv"},
            {"wmx", "video/x-ms-wmx"},
            {"wmz", "application/x-ms-wmz"},
            {"wpng", "image/x-up-wpng"},
            {"wpt", "x-lml/x-gps"},
            {"wri", "application/x-mswrite"},
            {"wrl", "x-world/x-vrml"},
            {"wrz", "x-world/x-vrml"},
            {"ws", "text/vnd.wap.wmlscript"},
            {"wsc", "application/vnd.wap.wmlscriptc"},
            {"wv", "video/wavelet"},
            {"wvx", "video/x-ms-wvx"},
            {"wxl", "application/x-wxl"},
            {"x-gzip", "application/x-gzip"},
            {"xar", "application/vnd.xara"},
            {"xbm", "image/x-xbitmap"},
            {"xdm", "application/x-xdma"},
            {"xdma", "application/x-xdma"},
            {"xdw", "application/vnd.fujixerox.docuworks"},
            {"xht", "application/xhtml+xml"},
            {"xhtm", "application/xhtml+xml"},
            {"xhtml", "application/xhtml+xml"},
            {"xla", "application/vnd.ms-excel"},
            {"xlc", "application/vnd.ms-excel"},
            {"xll", "application/x-excel"},
            {"xlm", "application/vnd.ms-excel"},
            {"xls", "application/vnd.ms-excel"},
            {"xlt", "application/vnd.ms-excel"},
            {"xlw", "application/vnd.ms-excel"},
            {"xm", "audio/x-mod"},
            {"xml", "text/xml"},
            {"xmz", "audio/x-mod"},
            {"xpi", "application/x-xpinstall"},
            {"xpm", "image/x-xpixmap"},
            {"xsit", "text/xml"},
            {"xsl", "text/xml"},
            {"xul", "text/xul"},
            {"xwd", "image/x-xwindowdump"},
            {"xyz", "chemical/x-pdb"},
            {"yz1", "application/x-yz1"},
            {"z", "application/x-compress"},
            {"zac", "application/x-zaurus-zac"},
            {"zip", "application/zip	"},
    };
    /**
     * The Table of ICON Types
     */
    public static final String[][] ICON_MapTable={
            {"3gp", "icon_video"},
            {"aab", "application/x-authoware-bin"},
            {"aam", "application/x-authoware-map"},
            {"aas", "application/x-authoware-seg"},
            {"ai", "application/postscript"},
            {"aif", "icon_audio"},
            {"aifc", "icon_audio"},
            {"aiff", "icon_audio"},
            {"als", "icon_audio"},
            {"amc", "application/x-mpeg"},
            {"ani", "application/octet-stream"},
            {"apk", "application/vnd.android.package-archive"},
            {"asc", "icon_text_plain"},
            {"asd", "application/astound"},
            {"asf", "icon_video"},
            {"asn", "application/astound"},
            {"asp", "application/x-asap"},
            {"asx", "icon_video"},
            {"au", "icon_audio"},
            {"avb", "application/octet-stream"},
            {"avi", "icon_video"},
            {"awb", "icon_audio"},
            {"bcpio", "application/x-bcpio"},
            {"bin", "application/octet-stream"},
            {"bld", "application/bld"},
            {"bld2", "application/bld2"},
            {"bmp", "icon_bmp"},
            {"bpk", "application/octet-stream"},
            {"bz2", "icon_archive"},
            {"c", "icon_c"},
            {"cpp", "icon_cpp"},
            {"cal", "image/x-cals"},
            {"ccn", "application/x-cnc"},
            {"cco", "application/x-cocoa"},
            {"cdf", "application/x-netcdf"},
            {"cgi", "magnus-internal/cgi"},
            {"chat", "application/x-chat"},
            {"class", "application/octet-stream"},
            {"clp", "application/x-msclip"},
            {"cmx", "application/x-cmx"},
            {"co", "application/x-cult3d-object"},
            {"cod", "image/cis-cod"},
            {"cpio", "application/x-cpio"},
            {"cpt", "application/mac-compactpro"},
            {"crd", "application/x-mscardfile"},
            {"csh", "application/x-csh"},
            {"csm", "chemical/x-csml"},
            {"csml", "chemical/x-csml"},
            {"css", "icon_css"},
            {"cur", "application/octet-stream"},
            {"dcm", "x-lml/x-evm"},
            {"dcr", "application/x-director"},
            {"dcx", "image/x-dcx"},
            {"dhtml", "icon_html"},
            {"dir", "application/x-director"},
            {"dll", "application/octet-stream"},
            {"dmg", "application/octet-stream"},
            {"dms", "application/octet-stream"},
            {"doc", "icon_doc"},
            {"dot", "icon_doc"},
            {"dvi", "application/x-dvi"},
            {"dwf", "drawing/x-dwf"},
            {"dwg", "application/x-autocad"},
            {"dxf", "application/x-autocad"},
            {"dxr", "application/x-director"},
            {"ebk", "application/x-expandedbook"},
            {"emb", "chemical/x-embl-dl-nucleotide"},
            {"embl", "chemical/x-embl-dl-nucleotide"},
            {"eps", "application/postscript"},
            {"eri", "image/x-eri"},
            {"es", "icon_audio"},
            {"esl", "icon_audio"},
            {"etc", "application/x-earthtime"},
            {"etx", "text/x-setext"},
            {"evm", "x-lml/x-evm"},
            {"evy", "application/x-envoy"},
            {"exe", "application/octet-stream"},
            {"fh4", "image/x-freehand"},
            {"fh5", "image/x-freehand"},
            {"fhc", "image/x-freehand"},
            {"fif", "image/fif"},
            {"fm", "application/x-maker"},
            {"fpx", "image/x-fpx"},
            {"fvi", "icon_video"},
            {"gau", "chemical/x-gaussian-input"},
            {"gca", "application/x-gca-compressed"},
            {"gdb", "x-lml/x-gdb"},
            {"gif", "icon_gif"},
            {"gps", "application/x-gps"},
            {"gtar", "application/x-gtar"},
            {"gz", "icon_gzip"},
            {"h", "icon_c_h"},
            {"hdf", "application/x-hdf"},
            {"hdm", "text/x-hdml"},
            {"hdml", "text/x-hdml"},
            {"hlp", "application/winhlp"},
            {"hqx", "application/mac-binhex40"},
            {"htm", "icon_html"},
            {"html", "icon_html"},
            {"hts", "icon_html"},
            {"ice", "x-conference/x-cooltalk"},
            {"ico", "icon_ico"},
            {"ief", "icon_image"},
            {"ifm", "icon_gif"},
            {"ifs", "icon_image"},
            {"imy", "icon_audio"},
            {"ins", "application/x-NET-Install"},
            {"ips", "application/x-ipscript"},
            {"ipx", "application/x-ipix"},
            {"it", "icon_audio"},
            {"itz", "icon_audio"},
            {"ivr", "i-world/i-vrml"},
            {"j2k", "icon_jpeg"},
            {"jad", "text/vnd.sun.j2me.app-descriptor"},
            {"jam", "application/x-jam"},
            {"java", "icon_java"},
            {"jar", "icon_java"},
            {"jnlp", "icon_java"},
            {"jpe", "icon_jpeg"},
            {"jpeg", "icon_jpeg"},
            {"jpg", "icon_jpeg"},
            {"jpz", "icon_jpeg"},
            {"js", "icon_javascript"},
            {"jwc", "application/jwc"},
            {"kjx", "application/x-kjx"},
            {"lak", "x-lml/x-lak"},
            {"latex", "application/x-latex"},
            {"lcc", "application/fastman"},
            {"lcl", "application/x-digitalloca"},
            {"lcr", "application/x-digitalloca"},
            {"lgh", "application/lgh"},
            {"lha", "application/octet-stream"},
            {"lml", "x-lml/x-lml"},
            {"lmlpack", "x-lml/x-lmlpack"},
            {"lsf", "icon_video"},
            {"lsx", "icon_video"},
            {"lzh", "application/x-lzh"},
            {"m13", "application/x-msmediaview"},
            {"m14", "application/x-msmediaview"},
            {"m15", "icon_audio"},
            {"m3u", "icon_playlist"},
            {"m3url", "icon_playlist"},
            {"ma1", "icon_audio"},
            {"ma2", "icon_audio"},
            {"ma3", "icon_audio"},
            {"ma5", "icon_audio"},
            {"man", "application/x-troff-man"},
            {"map", "magnus-internal/imagemap"},
            {"mbd", "application/mbedlet"},
            {"mct", "application/x-mascot"},
            {"mdb", "application/x-msaccess"},
            {"mdz", "icon_audio"},
            {"me", "application/x-troff-me"},
            {"mel", "text/x-vmel"},
            {"mi", "application/x-mif"},
            {"mid", "icon_audio"},
            {"midi", "icon_audio"},
            {"mif", "application/x-mif"},
            {"mil", "icon_image"},
            {"mio", "icon_audio"},
            {"mmf", "application/x-skt-lbs"},
            {"mng", "icon_video"},
            {"mny", "application/x-msmoney"},
            {"moc", "application/x-mocha"},
            {"mocha", "application/x-mocha"},
            {"mod", "icon_audio"},
            {"mof", "application/x-yumekara"},
            {"mol", "chemical/x-mdl-molfile"},
            {"mop", "chemical/x-mopac-input"},
            {"mov", "icon_video"},
            {"movie", "icon_video"},
            {"mp2", "icon_mp3"},
            {"mp3", "icon_mp3"},
            {"mp4", "icon_video"},
            {"mpc", "application/vnd.mpohun.certificate"},
            {"mpe", "icon_video"},
            {"mpeg", "icon_video"},
            {"mpg", "icon_video"},
            {"mpg4", "icon_video"},
            {"mpga", "icon_mp3"},
            {"mpn", "application/vnd.mophun.application"},
            {"mpp", "application/vnd.ms-project"},
            {"mps", "application/x-mapserver"},
            {"mrl", "text/x-mrml"},
            {"mrm", "application/x-mrm"},
            {"ms", "application/x-troff-ms"},
            {"mts", "application/metastream"},
            {"mtx", "application/metastream"},
            {"mtz", "application/metastream"},
            {"mzv", "application/metastream"},
            {"nar",  "icon_zip"},
            {"nbmp", "icon_image"},
            {"nc", "application/x-netcdf"},
            {"ndb", "x-lml/x-ndb"},
            {"ndwn", "application/ndwn"},
            {"nif", "application/x-nif"},
            {"nmz", "application/x-scream"},
            {"nokia-op-logo", "icon_image"},
            {"npx", "application/x-netfpx"},
            {"nsnd", "icon_audio"},
            {"nva", "application/x-neva1"},
            {"oda", "application/oda"},
            {"oom", "application/x-AtlasMate-Plugin"},
            {"pac", "icon_audio"},
            {"pae", "icon_audio"},
            {"pan", "application/x-pan"},
            {"pbm", "icon_bmp"},
            {"pcx", "icon_image"},
            {"pda", "icon_image"},
            {"pdb", "chemical/x-pdb"},
            {"pdf", "icon_pdf"},
            {"pfr", "application/font-tdpfr"},
            {"pgm", "icon_image"},
            {"pict", "icon_image"},
            {"pm", "application/x-perl"},
            {"pmd", "application/x-pmd"},
            {"png", "icon_png"},
            {"pnm", "icon_image"},
            {"pnz", "icon_png"},
            {"pot", "icon_ppt"},
            {"ppm", "icon_image"},
            {"pps", "icon_ppt"},
            {"ppt", "icon_ppt"},
            {"pqf", "application/x-cprplayer"},
            {"pqi", "application/cprplayer"},
            {"prc", "application/x-prc"},
            {"proxy", "application/x-ns-proxy-autoconfig"},
            {"ps", "application/postscript"},
            {"ptlk", "application/listenup"},
            {"pub", "application/x-mspublisher"},
            {"pvx", "icon_video"},
            {"qcp", "icon_audio"},
            {"qt", "icon_video"},
            {"qti", "icon_image"},
            {"qtif", "icon_image"},
            {"r3t", "text/vnd.rn-realtext3d"},
            {"ra", "icon_audio"},
            {"ram", "icon_audio"},
            {"rar", "icon_rar"},
            {"ras", "icon_image"},
            {"rdf", "application/rdf+xml"},
            {"rf", "icon_image"},
            {"rgb", "icon_image"},
            {"rlf", "application/x-richlink"},
            {"rm", "icon_audio"},
            {"rmf", "icon_audio"},
            {"rmm", "icon_audio"},
            {"rmvb", "icon_audio"},
            {"rnx", "application/vnd.rn-realplayer"},
            {"roff", "application/x-troff"},
            {"rp", "icon_image"},
            {"rpm", "icon_audio"},
            {"rt", "text/vnd.rn-realtext"},
            {"rte", "x-lml/x-gps"},
            {"rtf", "icon_text_richtext"},
            {"rtg", "application/metastream"},
            {"rtx", "text/richtext"},
            {"rv", "icon_video"},
            {"rwc", "application/x-rogerwilco"},
            {"s3m", "icon_audio"},
            {"s3z", "icon_audio"},
            {"sca", "application/x-supercard"},
            {"scd", "application/x-msschedule"},
            {"sdf", "application/e-score"},
            {"sea", "application/x-stuffit"},
            {"sgm", "text/x-sgml"},
            {"sgml", "text/x-sgml"},
            {"sh", "application/x-sh"},
            {"shar", "application/x-shar"},
            {"shtml", "icon_html"},
            {"shw", "application/presentations"},
            {"si6", "icon_image"},
            {"si7", "icon_image"},
            {"si9", "icon_image"},
            {"sis", "application/vnd.symbian.install"},
            {"sit", "application/x-stuffit"},
            {"skd", "application/x-Koan"},
            {"skm", "application/x-Koan"},
            {"skp", "application/x-Koan"},
            {"skt", "application/x-Koan"},
            {"slc", "application/x-salsa"},
            {"smd", "icon_audio"},
            {"smi", "application/smil"},
            {"smil", "application/smil"},
            {"smp", "application/studiom"},
            {"smz", "icon_audio"},
            {"snd", "icon_audio"},
            {"spc", "text/x-speech"},
            {"spl", "application/futuresplash"},
            {"spr", "application/x-sprite"},
            {"sprite", "application/x-sprite"},
            {"spt", "application/x-spt"},
            {"src", "application/x-wais-source"},
            {"stk", "application/hyperstudio"},
            {"stm", "icon_audio"},
            {"sv4cpio", "application/x-sv4cpio"},
            {"sv4crc", "application/x-sv4crc"},
            {"svf", "icon_image"},
            {"svg", "icon_image"},
            {"svh", "icon_image"},
            {"svr", "x-world/x-svr"},
            {"swf", "icon_flash"},
            {"swfl", "icon_flash"},
            {"t", "application/x-troff"},
            {"tad", "application/octet-stream"},
            {"talk", "text/x-speech"},
            {"tar", "icon_tar"},
            {"taz", "icon_tar"},
            {"tbp", "application/x-timbuktu"},
            {"tbt", "application/x-timbuktu"},
            {"tcl", "application/x-tcl"},
            {"tex", "application/x-tex"},
            {"texi", "application/x-texinfo"},
            {"texinfo", "application/x-texinfo"},
            {"tgz", "icon_tar"},
            {"thm", "application/vnd.eri.thm"},
            {"tif", "icon_tiff"},
            {"tiff", "icon_tiff"},
            {"tki", "application/x-tkined"},
            {"tkined", "application/x-tkined"},
            {"toc", "application/toc"},
            {"toy", "icon_image"},
            {"tr", "application/x-troff"},
            {"trk", "x-lml/x-gps"},
            {"trm", "application/x-msterminal"},
            {"tsi", "icon_audio"},
            {"tsp", "application/dsptype"},
            {"tsv", "text/tab-separated-values"},
            {"tsv", "text/tab-separated-values"},
            {"ttf", "application/octet-stream"},
            {"ttz", "application/t-time"},
            {"txt", "icon_text_plain"},
            {"ult", "icon_audio"},
            {"ustar", "application/x-ustar"},
            {"uu", "application/x-uuencode"},
            {"uue", "application/x-uuencode"},
            {"vcd", "application/x-cdlink"},
            {"vcf", "text/x-vcard"},
            {"vdo", "icon_video"},
            {"vib", "icon_audio"},
            {"viv", "icon_video"},
            {"vivo", "icon_video"},
            {"vmd", "application/vocaltec-media-desc"},
            {"vmf", "application/vocaltec-media-file"},
            {"vmi", "application/x-dreamcast-vms-info"},
            {"vms", "application/x-dreamcast-vms"},
            {"vox", "icon_audio"},
            {"vqe", "icon_audio"},
            {"vqf", "icon_audio"},
            {"vql", "icon_audio"},
            {"vre", "x-world/x-vream"},
            {"vrml", "x-world/x-vrml"},
            {"vrt", "x-world/x-vrt"},
            {"vrw", "x-world/x-vream"},
            {"vts", "workbook/formulaone"},
            {"wav", "icon_wav"},
            {"wax", "icon_audio"},
            {"wbmp", "icon_bmp"},
            {"web", "application/vnd.xara"},
            {"wi", "icon_image"},
            {"wis", "application/x-InstallShield"},
            {"wm", "icon_video"},
            {"wma", "icon_wma"},
            {"wmd", "application/x-ms-wmd"},
            {"wmf", "application/x-msmetafile"},
            {"wml", "text/vnd.wap.wml"},
            {"wmlc", "application/vnd.wap.wmlc"},
            {"wmls", "text/vnd.wap.wmlscript"},
            {"wmlsc", "application/vnd.wap.wmlscriptc"},
            {"wmlscript", "text/vnd.wap.wmlscript"},
            {"wmv", "icon_video"},
            {"wmx", "icon_video"},
            {"wmz", "application/x-ms-wmz"},
            {"wpng", "icon_png"},
            {"wpt", "x-lml/x-gps"},
            {"wri", "application/x-mswrite"},
            {"wrl", "x-world/x-vrml"},
            {"wrz", "x-world/x-vrml"},
            {"ws", "text/vnd.wap.wmlscript"},
            {"wsc", "application/vnd.wap.wmlscriptc"},
            {"wv", "icon_video"},
            {"wvx", "icon_video"},
            {"wxl", "application/x-wxl"},
            {"x-gzip", "application/x-gzip"},
            {"xar", "application/vnd.xara"},
            {"xbm", "icon_bmp"},
            {"xdm", "application/x-xdma"},
            {"xdma", "application/x-xdma"},
            {"xdw", "application/vnd.fujixerox.docuworks"},
            {"xht", "icon_xhtml_xml"},
            {"xhtm", "icon_xhtml_xml"},
            {"xhtml", "icon_xhtml_xml"},
            {"xla", "icon_xls"},
            {"xlc", "icon_xls"},
            {"xll", "application/x-excel"},
            {"xlm", "icon_xls"},
            {"xls", "icon_xls"},
            {"xlt", "icon_xls"},
            {"xlw", "icon_xls"},
            {"xm", "icon_audio"},
            {"xml", "icon_xml"},
            {"xmz", "icon_audio"},
            {"xpi", "application/x-xpinstall"},
            {"xpm", "icon_image"},
            {"xsit", "icon_xml"},
            {"xsl", "icon_xml"},
            {"xul", "text/xul"},
            {"xwd", "icon_image"},
            {"xyz", "chemical/x-pdb"},
            {"yz1", "application/x-yz1"},
            {"z", "application/x-compress"},
            {"zac", "application/x-zaurus-zac"},
            {"zip", "icon_zip"},
    };


}
