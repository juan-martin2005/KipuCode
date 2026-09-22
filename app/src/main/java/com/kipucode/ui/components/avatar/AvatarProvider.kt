package com.kipucode.ui.components.avatar

import com.kipucode.R
import com.kipucode.domain.model.Avatar

object AvatarProvider {

    val avatars: List<Avatar> = listOf(
        Avatar("avatar_000", R.drawable.avatar_000, "Avatar 0"),
        Avatar("avatar_001", R.drawable.avatar_001, "Avatar 1"),
        Avatar("avatar_002", R.drawable.avatar_002, "Avatar 2"),
        Avatar("avatar_003", R.drawable.avatar_003, "Avatar 3"),
        Avatar("avatar_004", R.drawable.avatar_004, "Avatar 4"),
        Avatar("avatar_005", R.drawable.avatar_005, "Avatar 5"),
        Avatar("avatar_006", R.drawable.avatar_006, "Avatar 6"),
        Avatar("avatar_007", R.drawable.avatar_007, "Avatar 7"),
        Avatar("avatar_008", R.drawable.avatar_008, "Avatar 8"),
        Avatar("avatar_009", R.drawable.avatar_009, "Avatar 9"),
        Avatar("avatar_010", R.drawable.avatar_010, "Avatar 10"),
        Avatar("avatar_011", R.drawable.avatar_011, "Avatar 11"),
        Avatar("avatar_012", R.drawable.avatar_012, "Avatar 12"),
        Avatar("avatar_013", R.drawable.avatar_013, "Avatar 13"),
        Avatar("avatar_014", R.drawable.avatar_014, "Avatar 14"),
        Avatar("avatar_015", R.drawable.avatar_015, "Avatar 15"),
        Avatar("avatar_016", R.drawable.avatar_016, "Avatar 16"),
        Avatar("avatar_017", R.drawable.avatar_017, "Avatar 17"),
        Avatar("avatar_018", R.drawable.avatar_018, "Avatar 18"),
        Avatar("avatar_019", R.drawable.avatar_019, "Avatar 19"),
        Avatar("avatar_020", R.drawable.avatar_020, "Avatar 20"),
        Avatar("avatar_021", R.drawable.avatar_021, "Avatar 21"),
        Avatar("avatar_022", R.drawable.avatar_022, "Avatar 22"),
        Avatar("avatar_023", R.drawable.avatar_023, "Avatar 23"),
        Avatar("avatar_024", R.drawable.avatar_024, "Avatar 24"),
        Avatar("avatar_025", R.drawable.avatar_025, "Avatar 25"),
        Avatar("avatar_026", R.drawable.avatar_026, "Avatar 26"),
        Avatar("avatar_027", R.drawable.avatar_027, "Avatar 27"),
        Avatar("avatar_028", R.drawable.avatar_028, "Avatar 28"),
        Avatar("avatar_029", R.drawable.avatar_029, "Avatar 29"),
        Avatar("avatar_030", R.drawable.avatar_030, "Avatar 30"),
        Avatar("avatar_031", R.drawable.avatar_031, "Avatar 31"),
        Avatar("avatar_032", R.drawable.avatar_032, "Avatar 32"),
        Avatar("avatar_033", R.drawable.avatar_033, "Avatar 33"),
        Avatar("avatar_034", R.drawable.avatar_034, "Avatar 34"),
        Avatar("avatar_035", R.drawable.avatar_035, "Avatar 35"),
        Avatar("avatar_036", R.drawable.avatar_036, "Avatar 36"),
        Avatar("avatar_037", R.drawable.avatar_037, "Avatar 37"),
        Avatar("avatar_038", R.drawable.avatar_038, "Avatar 38"),
        Avatar("avatar_039", R.drawable.avatar_039, "Avatar 39"),
        Avatar("avatar_040", R.drawable.avatar_040, "Avatar 40"),
        Avatar("avatar_041", R.drawable.avatar_041, "Avatar 41"),
        Avatar("avatar_042", R.drawable.avatar_042, "Avatar 42"),
        Avatar("avatar_043", R.drawable.avatar_043, "Avatar 43"),
        Avatar("avatar_044", R.drawable.avatar_044, "Avatar 44"),
        Avatar("avatar_045", R.drawable.avatar_045, "Avatar 45"),
        Avatar("avatar_046", R.drawable.avatar_046, "Avatar 46"),
        Avatar("avatar_047", R.drawable.avatar_047, "Avatar 47"),
        Avatar("avatar_048", R.drawable.avatar_048, "Avatar 48"),
        Avatar("avatar_049", R.drawable.avatar_049, "Avatar 49"),
        Avatar("avatar_050", R.drawable.avatar_050, "Avatar 50")
    )

    val defaultAvatar: Avatar = avatars.first()

    private val avatarMap: Map<String, Avatar> by lazy {
        avatars.associateBy { it.id }
    }

    fun getAvatarById(id: String?): Avatar {
        return avatarMap[id] ?: defaultAvatar
    }

    fun getDrawableById(id: String?): Int {
        return getAvatarById(id).resId
    }
}
