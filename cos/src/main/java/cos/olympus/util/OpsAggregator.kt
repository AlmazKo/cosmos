package cos.olympus.util

import cos.ops.ServiceOp
import cos.ops.SomeOp
import cos.ops.UserOp
import cos.ops.out.UserPackage

class OpsAggregator : OpConsumer {
    private val data = HashMap<Int, ArrayList<SomeOp>>()
    private var serviceOps = ArrayList<ServiceOp>()

    override fun add(op: SomeOp) {
        if (op is UserOp && op.userId() < 10000) {
            Ops.LOGGER.info(op, "out")
        }

        if (op is ServiceOp) {
            serviceOps.add(op)
            return
        }

        val usrId = if (op is UserOp) op.userId() else 0

        data.computeIfAbsent(usrId) { ArrayList() }
            .add(op)
    }

    fun size(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
    }

    val serviceData: List<SomeOp>
        get() = data.getOrDefault(0, listOf())

    fun userOps(): Map<Int, List<SomeOp>> {
        return data
    }

    fun serviceOps(): List<ServiceOp> {
        if (serviceOps.isEmpty()) return listOf()

        val tmp = serviceOps
        serviceOps = ArrayList()
        return tmp
    }

    fun adminOps(): List<SomeOp> {
        val tmp = data[0]
        return if ((tmp == null)) listOf() else tmp
    }

    fun getUserData(userId: Int): List<SomeOp> {
        require(userId != 0) { "UserId must be greater than 0" }

        val result = data[userId] ?: return listOf()

        return result
    }

    fun groupByUser(tick: Int, tickTime: Long): java.util.ArrayList<UserPackage> {
        val out = java.util.ArrayList<UserPackage>()
        data.forEach { userId, ops ->
            if (userId > 0 && userId < 10000) {
                val op = UserPackage(tick, tickTime, userId, ops.map { it as Record }.toTypedArray())
                out.add(op)
            }
        }
        return out
    }

}
