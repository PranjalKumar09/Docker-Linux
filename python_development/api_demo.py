import requests
def fetch_random_cat_fact():
    url  = "https://meowfacts.herokuapp.com"
    try :
        response = requests.get(url)
        response.raise_for_status() # check for any http errors
        
        fact = response.text
        return fact
    except requests.exceptions.RequestException as err:
        return f"An error occurred: {err}"
def main():
    fact = fetch_random_cat_fact()
    if isinstance(fact, str):
        print(fact)
    else:
        print("Error fetching cat fact.")
if __name__ == "__main__":
    main()