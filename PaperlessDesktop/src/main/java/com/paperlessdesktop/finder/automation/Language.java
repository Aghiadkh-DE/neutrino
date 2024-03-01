package com.paperlessdesktop.finder.automation;

/**
 * Enum that holds all the languages the Tesseract supports.
 */
public enum Language{
    English("eng"), Afrikaans("afr"), Amharic("amh"), Arabic("ara"), Assamese("asm"), 
    Azerbaijani("aze"), Azerbaijani_Cyrilic("aze_cyrl"), Belarusian("bel"), Bengali("ben"),
    Tibetan("bod"), Bosnian("bos"), Breton("bre"), Bulgarian("bul"), Catalan_Valencian("cat"),
    Cebuano("ceb"), Czech("ces"), Chinese_Simplified("chi_sim"), Chinese_Traditional("chi_tra"),
    Cherokee("chr"), Corsican("cos"), Welsh("cym"), Danish("dan"), German("deu"), Dzongkha("dzo"),
    Greek_Modern_1453("ell"), English_Middle_1100_1500("enm"), Esperanto("epo"), Estonian("est"),
    Basque("eus"), Faroese("fao"), Persian("fas"), Filipino_old_Tagalog("fil"), Finnish("fin"),
    French("fra"), German_Fraktur("frk"), French_Middle_1400_1600("frm"), Western_Frisian("fry"),
    Scottish_Gaelic("gla"), Irish("gle"), Galician("glg"), Greek_Ancient_to_1453("grc"), Gujarati("guj"),
    Haitian_Creole("hat"), Hebrew("heb"), Hindi("hin"), Croatian("hrv"), Hungarian("hun"), Armenian("hye"),
    Inuktitut("iku"), Indonesian("ind"), Icelandic("isl"), Italian("ita"), 	Italian_Old("ita_old"), 
    Javanese("jav"), Japanese("jap"), Kannada("kan"), Georgian("kat"), Georgian_Old("kat_old"),
    Kazakh("kaz"), Central_Khmer("khm"), Kirghiz("kir"), Kurmanji("kmr"), Korean("kor"), Korean_vertical("kor_vert"),
    Kurdish_Arabic_Script("kur"), Lao("lao"), Latin("lat"), Latvian("lav"), Lithuanian("lit"), Luxembourgish("itz"),
    Malayalam("mal"), Marathi("mar"), Macedonian("mkd"), Maltese("mlt"), Mongolian("mon"), Maori("mri"),
    Malay("msa"), Burmese("mya"), Nepali("nep"), Dutch_Flemish("nld"), Norwegian("nor"), Occitan_post_1500("oci"),
    Oriya("ori"), Panjabi("pan"), Polish("pol"), Portuguese("por"), Pashto("pus"), Quechua("que"), Romanian("ron"),
    Russian("rus"), Sanskrit("san"), Sinhala("sin"), Slovak("slk"), Slovak_Fraktur("slk_frak"), Slovenian("slv"),
    Sindhi("snd"), Spanish("spa"), Castilian_Old("spa_old"), Albanian("sqi"), Serbian("srp"), Serbian_Latin("srp_latn"),
    Sundanese("sun"), Swahili("swa"), Swedish("swe"), Syriac("syr"), Tamil("tam"), Tatar("tat"), Telugu("tel"),
    Tajik("tgk"), Tagalog("tgl"), Thai("tha"), Tigrinya("tir"), Tonga("ton"), Turkish("tur"), Uyghur("uig"),
    Ukrainian("ukr"), Urdu("urd"), Uzbek("uzb"), Uzbek_Cyrilic("uzb_cyrl"), Vietnamese("vie"), Yiddish("yid"),
    Yoruba("yor");
    private final String identifier;

    Language(String identifier){
        this.identifier = identifier;
    }

    /**
     * The abbreviation of the language.
     * @return String that represents the abbreviation.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return super.toString();
    }



}
