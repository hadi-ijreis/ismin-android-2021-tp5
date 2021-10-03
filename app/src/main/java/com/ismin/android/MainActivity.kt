package com.ismin.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), BookCreator {

    private val TAG = MainActivity::class.java.simpleName

    private val bookshelf = Bookshelf()
    private lateinit var btnCreateBook: FloatingActionButton

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://bookshelf-gme.cleverapps.io/")
        .build()
    val bookService = retrofit.create(BookService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreateBook = findViewById(R.id.a_main_btn_create_book)
        btnCreateBook.setOnClickListener {
            displayCreateBook()
        }

        loadAllBooks()
    }

    private fun loadAllBooks() {
        bookService.getAllBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(
                call: Call<List<Book>>,
                response: Response<List<Book>>
            ) {
                val allBooks: List<Book>? = response.body()

                allBooks?.forEach {
                    bookshelf.addBook(it)
                }
                displayBookList();
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error when trying to fetch books" + t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
        )
    }

    private fun displayBookList() {
        btnCreateBook.visibility = View.VISIBLE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = BookListFragment.newInstance(bookshelf.getAllBooks())
        fragmentTransaction.replace(R.id.a_main_lyt_fragment_container, fragment)
        fragmentTransaction.commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_delete -> {
                bookshelf.clear()
                displayBookList()
                true
            }
            // If we got here, the user's action was not recognized.
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayCreateBook() {
        btnCreateBook.visibility = View.GONE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = CreateBookFragment.newInstance()
        fragmentTransaction.replace(R.id.a_main_lyt_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onBookCreated(book: Book) {
        bookshelf.addBook(book);
        displayBookList()
    }
}