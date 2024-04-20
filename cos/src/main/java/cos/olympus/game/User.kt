package cos.olympus.game

data class User(
    val id: Int,
    var worldName: String
) {
    val name: String = "user:$id"
}
