package ru.nsu.manasyan.zebra.model

enum class QueryType {
    PQF,
    CQL
}

data class SearchRequest(
    val query: String,
    val type: QueryType = QueryType.PQF,
    val startRecord: Int? = null,
    val maximumRecords: Int? = null,
    val recordSchema: String? = null,
    val sortKeys: String? = null
)

data class SearchResponse(
    val numberOfRecords: Int,
    val records: List<SearchRecord>,
    val diagnostics: List<SearchDiagnostics>
)

data class SearchRecord(
    val recordPosition: Int,
    val recordIdentifier: String,
    val recordSchema: String,
    val recordData: String
)

data class SearchDiagnostics(
    val message: String,
    val details: String,
)

data class ScanRequest(
    val scanClause: String,
    val type: QueryType = QueryType.PQF,
    val number: Int? = null,
    val position: Int? = null
)


data class ScanResponse(
    val terms: List<ScanTerm>,
    val diagnostics: List<SearchDiagnostics>
)

data class ScanTerm(
    val numberOfRecords: Int,
    val value: String,
    val displayTerm: String
)
