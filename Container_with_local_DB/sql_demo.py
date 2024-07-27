import pymysql

def create_connection():
    try:
        connection = pymysql.connect(
            host="host.docker.internal",
            user="root",
            password="1234",
            db="myuserinfo"
        )
        return connection
    except pymysql.MySQLError as e:
        print(f"Error connecting to MySQL: {e}")
        return None

def create_table(connection):
    if connection:
        cursor = connection.cursor()
        try:
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS names (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                )
            ''')
            connection.commit()
        except pymysql.MySQLError as e:
            print(f"Error creating table: {e}")
        finally:
            cursor.close()

def fetch_all_names(connection):
    names = []
    if connection:
        cursor = connection.cursor()
        try:
            cursor.execute("SELECT name FROM names")
            names = [row[0] for row in cursor.fetchall()]
        except pymysql.MySQLError as e:
            print(f"Error fetching names: {e}")
        finally:
            cursor.close()
    return names

def insert_name(connection, name):
    if connection:
        cursor = connection.cursor()
        try:
            cursor.execute("INSERT INTO names (name) VALUES (%s)", (name,))
            connection.commit()
        except pymysql.MySQLError as e:
            print(f"Error inserting name: {e}")
        finally:
            cursor.close()

def main():
    connection = create_connection()
    if not connection:
        print("Failed to connect to the database. Please check your credentials and try again.")
        return

    create_table(connection)

    while True:
        print("\nChoose an option:")
        print("1. Insert a new name")
        print("2. Display all names")
        print("3. Exit")

        choice = input("Enter your choice: ")

        if choice == "1":
            name = input("Enter a name: ")
            insert_name(connection, name)
            print(f"Name '{name}' inserted successfully.")

        elif choice == "2":
            names = fetch_all_names(connection)
            print("\nAll names:")
            for name in names:
                print(name)

        elif choice == "3":
            if connection:
                connection.close()
            print("Goodbye!")
            break

        else:
            print("Invalid choice. Please try again.")

if __name__ == "__main__":
    main()
