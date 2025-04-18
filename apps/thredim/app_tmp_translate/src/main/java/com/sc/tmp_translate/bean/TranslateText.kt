package com.sc.tmp_translate.bean

class TranslateText {

    var ResponseMetadata: ResponseMetaData? = null
    var TranslationList: List<TranslationItem> = arrayListOf()

    class ResponseMetaData {
        var Action: String = ""
        var Region: String = ""
        var RequestId: String = ""
        var Service: String = ""
        var Version: String = ""
    }

    class TranslationItem {
        var DetectedSourceLanguage: String = ""
        var Extra: ExtraItem? = null
        var Translation: String = ""
    }

    class ExtraItem {
        var input_characters: String = ""
        var source_language: String = ""
    }

}