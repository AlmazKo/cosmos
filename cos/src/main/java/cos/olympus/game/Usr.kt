package cos.olympus.game

class Usr(
    val id: Int,
    var worldName: String
) {
    val name: String = "user:$id"

    override fun toString(): String {
        return "Usr{" +
            "id=" + id +
            ", worldName='" + worldName + '\'' +
            '}'
    }
}
