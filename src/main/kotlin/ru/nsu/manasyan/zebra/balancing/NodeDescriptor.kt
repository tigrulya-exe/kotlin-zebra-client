package ru.nsu.manasyan.zebra.balancing

class NodeDescriptor private constructor(
    val namespace: String,
    val maxRepoSize: Int,
    val diskSize: Int,
    val ramSize: Int,
    val processorsCount: Int,
) {
    var repoAmount: Int = 0

    val canFitRepo: Boolean
        get() = 2 * maxRepoSize * repoAmount < diskSize

    companion object {
        fun of(
            namespace: String,
            maxRepoSize: Int,
            diskSize: Int,
            ramSize: Int,
            processorsCount: Int
        ): NodeDescriptor {
            validateRange(maxRepoSize, NodeDescriptorConfig.maxRepoSizeRange, "Repo size")
            validateRange(diskSize, NodeDescriptorConfig.diskSizeRange, "Disk size")
            validateRange(ramSize, NodeDescriptorConfig.ramSizeRange, "RAM size")
            validateRange(processorsCount, NodeDescriptorConfig.processorsCountRange, "Processors count")

            return NodeDescriptor(
                namespace,
                maxRepoSize,
                diskSize,
                ramSize,
                processorsCount
            )
        }

        private fun validateRange(value: Int, range: Pair<Int, Int>, name: String) {
            if (value < range.first || value > range.second) {
                throw RuntimeException("$name should be between $range.first $range.second")
            }
        }
    }
}

object NodeDescriptorConfig {
    var maxRepoSizeRange: Pair<Int, Int> = Pair(10, 30)
    var diskSizeRange: Pair<Int, Int> = Pair(100, 1000)
    var ramSizeRange: Pair<Int, Int> = Pair(1, 8)
    var processorsCountRange: Pair<Int, Int> = Pair(1, 4)
}

