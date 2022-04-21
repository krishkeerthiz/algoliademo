package com.example.algoliademo1.ui

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import com.example.algoliademo1.model.ProductInfo
import com.example.algoliademo1.model.ProductModel
import io.ktor.client.features.logging.*

class MyViewModel : ViewModel() {
    var productView: View?  = null
    val client = ClientSearch(
        ApplicationID("9N1YDJJ8DK"),
        APIKey("dcd5088a151c2e8db47aec60ea0eb6ec"),
        LogLevel.ALL
    )
    val index = client.initIndex(IndexName("products4"))
    val searcher = SearcherSingleIndex(index)

    val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        ProductInfo(
            hit.json["objectID"].toString(),
            hit.json["name"].toString()
        )
    }

    val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()

    val products: LiveData<PagedList<ProductInfo>> =
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val productModels = MutableLiveData<PagedList<ProductModel>>()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))

    val stats = StatsConnector(searcher)

    val filterState = FilterState()

    val clearAll = FilterClearConnector(filterState = filterState)

    val facetList = FacetListConnector(
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

    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats

        connection += facetList
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


}