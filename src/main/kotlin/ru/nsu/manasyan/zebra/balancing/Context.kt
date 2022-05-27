package ru.nsu.manasyan.zebra.balancing

interface Context {
    val nodes: List<NodeDescriptor>
    val maxNodesCount: Int

    fun addNode(node: NodeDescriptor)

    fun addNodes(nodes: List<NodeDescriptor>) {
        nodes.forEach { addNode(it) }
    }
}

class MutableContext(
    override val maxNodesCount: Int
) : Context {
    override val nodes: MutableList<NodeDescriptor> = mutableListOf()

    override fun addNode(node: NodeDescriptor) {
        if (nodes.size >= maxNodesCount) {
            throw RuntimeException("No more capacity for node $node")
        }
        nodes.add(node)
    }
}