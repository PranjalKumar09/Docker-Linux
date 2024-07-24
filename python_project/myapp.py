import os

user_name = input("Enter your name to store in file or press Enter to proceed: ")
if user_name:
    # Ensure the directory exists
    os.makedirs("python_project", exist_ok=True)
    with open("python_project/user_info.txt", "a") as file:
        file.write(user_name + "\n")

show_info = input("Do you want to see all user names? y/n: ")
if show_info.lower() == 'y':
    try:
        with open("python_project/user_info.txt", "r") as file:
            content = file.readlines()
    except Exception as e:
        print(e, type(e))
    else:
        for line in content:
            print(f"{line.rstrip()}")
