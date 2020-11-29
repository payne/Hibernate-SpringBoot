
https://learning.oreilly.com/library/view/spring-boot-persistence/9781484256268/html/487471_1_En_1_Chapter.xhtml#Fn3

Here's a copy & paste:

Item 4: How to Effectively Shape the @ManyToMany Association
This time, the well-known Author and Book entities are involved in a bidirectional lazy @ManyToMany association (an author has written more books and a book was written by several authors). See Figure 1-6.
../images/487471_1_En_1_Chapter/487471_1_En_1_Fig6_HTML.jpg
Figure 1-6The @ManyToMany table relationship
The bidirectional @ManyToMany association can be navigated from both sides, therefore, both sides can be parents (parent-side). Since both are parents, none of them will hold a foreign key. In this association, there are two foreign keys that are stored in a separate table, known as the junction or join table. The junction table is hidden and it plays the child-side role.

The best way to code a bidirectional @ManyToMany association is described in the following sections.

CHOOSE THE OWNER OF THE RELATIONSHIP
Using the default @ManyToMany mapping requires the developer to choose an owner of the relationship and a mappedBy side (aka, the inverse side). Only one side can be the owner and the changes are only propagated to the database from this particular side. For example, Author can be the owner, while Book adds a mappedBy side.
@ManyToMany(mappedBy = "books")
private Set<Author> authors = new HashSet<>();
ALWAYS USE SET NOT LIST
Especially if remove operations are involved, it is advisable to rely on Set and avoid List. As Item 5 highlights, Set performs much better than List.
private Set<Book> books = new HashSet<>();     // in Author
private Set<Author> authors = new HashSet<>(); // in Book
KEEP BOTH SIDES OF THE ASSOCIATION IN SYNC
You can easily keep both sides of the association in sync via helper methods added on the side that you are more likely to interact with. For example, if the business logic is more interested in manipulating Author than Book then the developer can add Author to least these three helpers: addBook(), removeBook() and removeBooks().

AVOID CASCADETYPE.ALL AND CASCADETYPE.REMOVE
In most of the cases, cascading removals are bad ideas. For example, removing an Author entity should not trigger a Book removal because the Book can be referenced by other authors as well (a book can be written by several authors). So, avoid CascadeType.ALL and CascadeType.REMOVE and rely on explicit CascadeType.PERSIST and CascadeType.MERGE:
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private Set<Book> books = new HashSet<>();
The orphan removal (orphanRemoval) option is defined on @OneToOne and @OneToMany relationship annotations, but on neither of the @ManyToOne or @ManyToMany annotations.

SETTING UP THE JOIN TABLE
Explicitly setting up the join table name and the columns names allows the developer to reference them without confusion. This can be done via @JoinTable as in the following example:
@JoinTable(name = "author_book",
          joinColumns = @JoinColumn(name = "author_id"),
          inverseJoinColumns = @JoinColumn(name = "book_id")
)
USING LAZY FETCHING ON BOTH SIDES OF THE ASSOCIATION
By default, the @ManyToMany association is lazy. Keep it this way! Don’t do this:
@ManyToMany(fetch=FetchType.EAGER)
OVERRIDE EQUALS() AND HASHCODE()
By properly overriding the equals() and hashCode() methods , the application obtains the same results across all entity state transitions. This aspect is dissected in Item 68. For bidirectional @ManyToMany associations, these methods should be overridden on both sides.

PAY ATTENTION TO HOW TOSTRING() IS OVERRIDDEN
If toString() needs to be overridden, involve only the basic attributes fetched when the entity is loaded from the database. Involving lazy attributes or associations will trigger separate SQL statements for fetching the corresponding data.

AUTHOR AND BOOK SAMPLES
Gluing these instructions together and expressing them in code will result in the following Author and Book samples :
@Entity
public class Author implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String genre;
    private int age;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "author_book",
              joinColumns = @JoinColumn(name = "author_id"),
              inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();
    public void addBook(Book book) {
        this.books.add(book);
        book.getAuthors().add(this);
    }
    public void removeBook(Book book) {
        this.books.remove(book);
        book.getAuthors().remove(this);
    }
    public void removeBooks() {
        Iterator<Book> iterator = this.books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            book.getAuthors().remove(this);
            iterator.remove();
        }
    }
    // getters and setters omitted for brevity
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return id != null && id.equals(((Author) obj).id);
    }
    @Override
    public int hashCode() {
        return 2021;
    }
    @Override
    public String toString() {
        return "Author{" + "id=" + id + ", name=" + name
                      + ", genre=" + genre + ", age=" + age + '}';
    }
}
@Entity
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String isbn;
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();
    // getters and setter omitted for brevity
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return id != null && id.equals(((Book) obj).id);
    }
    @Override
    public int hashCode() {
        return 2021;
    }
    @Override
    public String toString() {
        return "Book{" + "id=" + id + ", title=" + title
                                     + ", isbn=" + isbn + '}';
    }
}
The source code is available on GitHub4.

Alternatively, @ManyToMany can be replaced with two bidirectional @OneToMany associations. In other words, the junction table can be mapped to an entity. This comes with several advantages, discussed in this article5.

