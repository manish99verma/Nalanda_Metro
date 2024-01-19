package com.manish.nalandametro.graph

class CitySearchManager(private val cities: List<String>) {
    data class Trie(
        val children: MutableMap<Char, Trie> = mutableMapOf(),
        var isEnd: Boolean = false,
        var city: String? = null
    )

    private val root = Trie()

    init {
        cities.forEach {
            var curr = root
            for (ch in it) {
                if (ch == ' ') continue
                val c = Character.toLowerCase(ch)

                if (curr.children[c] == null)
                    curr.children[c] = Trie()
                curr = curr.children[c]!!
            }

            curr.isEnd = true
            curr.city = it
        }
    }

    fun filterCities(query: String, limitToTop: Int): List<String> {
        if (query.isEmpty() || cities.isEmpty())
            return emptyList()


        var curr = root
        for (c in query) {
            if (c == ' ')
                continue

            val ch = Character.toLowerCase(c)
            if (curr.children[ch] == null)
                return emptyList()

            curr = curr.children[ch]!!
        }

        val result = mutableListOf<String>()
        addAllCities(result, curr, limitToTop)
        return result
    }

    private fun addAllCities(list: MutableList<String>, root: Trie?, limitToTop: Int) {
        if (root == null || limitToTop == list.size)
            return
        if (root.isEnd)
            list.add(root.city!!)
        root.children.forEach {
            addAllCities(list, it.value, limitToTop)
        }
    }

    fun getCities() = cities
}