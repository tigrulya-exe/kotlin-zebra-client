package ru.nsu.manasyan.zebra.balancing

import org.junit.jupiter.api.Test
import kotlin.math.min

internal class RepositoryBalancerTest {
    companion object {
        const val MAX_NODES_COUNT = 15
    }

    private val balancer = RepositoryBalancer()

    private fun buildDefaultNode(namespace: String) = NodeDescriptor.of(
        namespace = namespace,
        maxRepoSize = 10,
        diskSize = 300,
        ramSize = 2,
        processorsCount = 1
    )

    @Test
    fun testEqualNodes() {
        val nodes = (0 until MAX_NODES_COUNT)
            .map { buildDefaultNode("Node-$it") }
        val context = MutableContext(MAX_NODES_COUNT).apply {
            addNodes(nodes)
        }

        repeat(100) {
            val result = balancer.selectNamespace(context)
            if (result is RepositoryBalancer.Result.NotFound) {
                println("Error balancing during $it iteration: ${result.reason}")
            }
        }

        nodes.forEach { println("${it.namespace} - ${it.repoAmount}") }
    }

    @Test
    fun testUnequalNodes() {
        val smallNode = buildDefaultNode("Small node")
        val nodes = listOf(
            smallNode,
            multiplySpec(smallNode, "Medium node", 2),
            multiplySpec(smallNode, "Big node", 3)
        )

        val context = MutableContext(MAX_NODES_COUNT).apply {
            addNodes(nodes)
        }

        repeat(50) {
            val result = balancer.selectNamespace(context)
            if (result is RepositoryBalancer.Result.NotFound) {
                println("Error balancing during $it iteration: ${result.reason}")
            }
        }

        nodes.forEach { println("${it.namespace} - ${it.repoAmount}") }
    }

    private fun multiplySpec(node: NodeDescriptor, namespace: String, times: Int) = NodeDescriptor.of(
        namespace = namespace,
        maxRepoSize = min(node.maxRepoSize * times, NodeDescriptorConfig.maxRepoSizeRange.second),
        diskSize = min(node.diskSize * times, NodeDescriptorConfig.diskSizeRange.second),
        ramSize = min(node.ramSize * times, NodeDescriptorConfig.ramSizeRange.second),
        processorsCount = min(node.processorsCount * times, NodeDescriptorConfig.processorsCountRange.second)
    )
}