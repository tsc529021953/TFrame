package com.sc.tmp_translate.bean

class TranslateBean {

    var Subtitle: SubtitleBean? = null
    var ResponseMetaData: TranslateText.ResponseMetaData? = null

    public class SubtitleBean {
        var Text: String = ""
        var BeginTime: Int = 0
        var EndTime: Int = 0
        var Definite: Boolean = false
        var Language: String = ""
        var Sequence: Int = 0
    }


}