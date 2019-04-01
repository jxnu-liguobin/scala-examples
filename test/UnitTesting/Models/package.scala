package UnitTesting

package object Models {

    /**
     * Play does not require models to use a particular database data access layer.
     * However, if the application uses Anorm or Slick, then frequently the Model will have a reference to database access internally.
     *
     *
     * For unit testing, this approach can make mocking out the roles method tricky.
     *
     * A common approach is to keep the models isolated from the database and as much logic as possible,
     * and abstract database access behind a repository layer.
     */
}
