package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-02-27-13:59
 * @Email qiukeling@nbhope.cn
 */
class QtAlbum {
    /**
     * description : 米乐英语北美外教为大家送上绘声绘色的睡前英语故事！
    100天带你畅读100本世界经典绘本
    每天5分钟，带你聆听世界上最好的绘本故事
    关注围信公zhong号：KKTalkee ，获取精美电子绘本。
     * title : 北美外教读睡前故事
     * program_count : 196
     * id : 236513
     * thumbs : {"small_thumb":"http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!200","large_thumb":"http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!800","medium_thumb":"http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!400"}
     */

    val title: String? = null
    val description: String? = null
    val program_count = 0
    val id = 0
    val thumbs: ThumbsBean? = null

    class ThumbsBean {
        /**
         * small_thumb : http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!200
         * large_thumb : http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!800
         * medium_thumb : http://open.nbhope.cn/2018/01/05/partner_341243e183167efcf0cbf2abc04384dc.png!400
         */
        var small_thumb: String? = null
        var large_thumb: String? = null
        var medium_thumb: String? = null

    }
}