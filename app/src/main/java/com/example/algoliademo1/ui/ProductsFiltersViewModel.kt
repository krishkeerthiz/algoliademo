package com.example.algoliademo1.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.helper.android.filter.state.connectPagedList
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.filter.clear.FilterClearConnector
import com.algolia.instantsearch.helper.filter.facet.FacetListConnector
import com.algolia.instantsearch.helper.filter.facet.FacetListPresenterImpl
import com.algolia.instantsearch.helper.filter.facet.FacetSortCriterion
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.searcher.connectFilterState
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.model.ProductInfo
import io.ktor.client.features.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsFiltersViewModel : ViewModel() {

    private val client = ClientSearch(
        ApplicationID(ApplicationID),
        APIKey(APIKEY),
        LogLevel.ALL
    )
    private val index = client.initIndex(IndexName("products4"))
    private val searcher = SearcherSingleIndex(index)

    private val productsRepository = ProductsRepository.getRepository()

    private val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        ProductInfo(
            hit.json["objectID"].toString(),
            hit.json["name"].toString()
        )
    }

    private val connection = ConnectionHandler()

    private val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()

    val products: LiveData<PagedList<ProductInfo>> =
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))

    val stats = StatsConnector(searcher)

    private val filterState = FilterState()

    val clearAll = FilterClearConnector(filterState = filterState)

    val facetList1 = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("categories"),
        selectionMode = SelectionMode.Single
    )

    val facetList2 = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("type"),
        selectionMode = SelectionMode.Single
    )
    val facetList3 = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("brand"),
        selectionMode = SelectionMode.Single
    )

    val facetList4 = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("price_range"),
        selectionMode = SelectionMode.Single
    )

    val facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending, FacetSortCriterion.IsRefined),
        limit = 100
    )

    init {
        connection += searchBox
        connection += stats

        connection += facetList1
        connection += facetList2
        connection += facetList3
        connection += facetList4
        connection += searcher.connectFilterState(filterState)
        connection += filterState.connectPagedList(products)
        connection += clearAll

    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.clear()

    }

    suspend fun getProducts(productInfos: List<ProductInfo>?): List<Product?> {
        val products = mutableListOf<Product?>()

        if (productInfos != null) {
            for(productInfo in productInfos){
                viewModelScope.launch(Dispatchers.IO) {
                    products.add(getProduct(productInfo.id))
                }.join()
            }
        }

        return products
    }

     suspend fun getProduct(productId: String) = withContext(Dispatchers.IO){
         productsRepository.getProduct(productId.removeSurrounding("\"", "\""))
     }


    companion object{
        private const val ApplicationID = "9N1YDJJ8DK"
        private const val APIKEY = "dcd5088a151c2e8db47aec60ea0eb6ec"

    }

}