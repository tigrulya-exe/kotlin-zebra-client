package ru.nsu.manasyan.zebra.balancing


class RepositoryBalancer {

    sealed interface Result {
        data class Success(val namespace: String) : Result
        data class NotFound(val reason: String) : Result
    }

    fun selectNamespace(context: Context): Result {
        val (bestNode, score) = context
            .nodes
            .map { Pair(it, calculateScore(it)) }
            .maxByOrNull { it.second }
            ?: return Result.NotFound("Nodes not found in the context")

        if (score == 0.0) {
            return Result.NotFound("Out of space")
        }

        bestNode.repoAmount++
        return Result.Success(bestNode.namespace)
    }

    private fun calculateScore(node: NodeDescriptor) = with(node) {
        val canFitMultiplier = if (canFitRepo) 1 else 0
        canFitMultiplier *
                (0.5 * (diskSize - 2 * maxRepoSize * repoAmount) / diskSize
                        + 0.2 * processorsCount / NodeDescriptorConfig.processorsCountRange.second
                        + 0.2 * ramSize / NodeDescriptorConfig.ramSizeRange.second
                        + 0.1 * maxRepoSize / diskSize)
    }
}
