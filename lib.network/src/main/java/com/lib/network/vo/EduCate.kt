package com.lib.network.vo

/**
 *  Create: enjie
 *  Date: 2021/4/12
 *  Describe:
 */
data class EduCate(
        val cataHit: Int,
        val cataIcon: String,
        val cataName: String,
        val cataNo: String,
        val cataOrder: Int,
        val cataState: Boolean,
        val child: List<EduChildCate>?,
        val createTime: Long,
        val createUser: Long,
        val delState: Boolean,
        val parentId: String,
        val parentIds: String,
        val refrenceId: String,
        val updateTime: Long,
        val updateUser: Long,
        override val itemType: Int = 0
) : MultiItemEntity {
    override fun toString(): String {
        return "EduCate(cataHit=$cataHit, cataIcon='$cataIcon', cataName='$cataName', cataNo='$cataNo', cataOrder=$cataOrder, cataState=$cataState, child=$child, createTime=$createTime, createUser=$createUser, delState=$delState, parentId='$parentId', parentIds='$parentIds', refrenceId='$refrenceId', updateTime=$updateTime, updateUser=$updateUser, itemType=$itemType)"
    }
}

data class EduChildCate(
        val cataHit: Int,
        val cataIcon: String,
        val cataName: String,
        val cataNo: String,
        val cataOrder: Int,
        val cataState: Boolean,
        val createTime: Long,
        val createUser: Long,
        val delState: Boolean,
        val parentId: String,
        val parentIds: String,
        val refrenceId: String,
        val updateTime: Long,
        val updateUser: Long,
        override var itemType: Int = 1,
        var cateTitle:String
) : MultiItemEntity{
    override fun toString(): String {
        return "EduChildCate(cataHit=$cataHit, cataIcon='$cataIcon', cataName='$cataName', cataNo='$cataNo', cataOrder=$cataOrder, cataState=$cataState, createTime=$createTime, createUser=$createUser, delState=$delState, parentId='$parentId', parentIds='$parentIds', refrenceId='$refrenceId', updateTime=$updateTime, updateUser=$updateUser, itemType=$itemType)"
    }
}
