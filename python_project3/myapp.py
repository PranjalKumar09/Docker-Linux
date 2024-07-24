
try:
    with open('servers.txt', 'r') as file:
        content = file.readlines()
except Exception as e:
    print(f'Error reading the file: {e}')
else:
    servers = [line.strip() for line in content]
    print('List of servers:')
    for server in servers:
        print(server)