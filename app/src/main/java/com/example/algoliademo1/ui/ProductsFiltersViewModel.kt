package com.example.algoliademo1.ui

import android.content.Context
import androidx.lifecycle.*
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
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.example.algoliademo1.data.source.local.entity.Product
import com.example.algoliademo1.data.source.remote.AlgoliaIndex
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.model.ProductInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductsFiltersViewModel(context: Context) : ViewModel() {

//    private val client = ClientSearch(
//        ApplicationID(ApplicationID),
//        APIKey(APIKEY),
//        LogLevel.ALL
//    )
    private val index = AlgoliaIndex.getClient().initIndex(IndexName("products4"))
    private val searcher = SearcherSingleIndex(index)

    private val productsRepository = ProductsRepository.getRepository(context)

    private val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        ProductInfoModel(
            hit.json["objectID"].toString(),
            hit.json["name"].toString()
        )
    }

    private val connection = ConnectionHandler()

    private val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()

    val products: LiveData<PagedList<ProductInfoModel>> =
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    var pendingHits: PagedList<ProductInfoModel>? = null

    var running = MutableLiveData<Boolean>()

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

    suspend fun getProducts(productInfos: List<ProductInfoModel>?): List<Product?> {
        return withContext(Dispatchers.Default){
            val products = mutableListOf<Product?>()

            if (productInfos != null)
                for(productInfo in productInfos)
                    products.add(getProduct(productInfo.id))

            products
        }
    }

     suspend fun getProduct(productId: String) =
         productsRepository.getProduct(productId.removeSurrounding("\"", "\""))

}

class ProductsFiltersViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsFiltersViewModel(context) as T
    }
}