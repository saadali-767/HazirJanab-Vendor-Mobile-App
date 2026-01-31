import os
from lxml import etree as ET
import psutil
import socket

# Get the parent folder path
parent_folder = os.path.abspath(os.path.join(os.getcwd(), os.pardir))

# Define the path to network_config.xml and network_security_config.xml
network_config_path = os.path.join(parent_folder, './app/src/main/res/values/', 'network_config.xml')
network_security_config_path = os.path.join(parent_folder, './app/src/main/res/xml/', 'network_security_config.xml')

def get_wifi_ip():
    interfaces = psutil.net_if_addrs()
    for interface_name, interface_addresses in interfaces.items():
        if interface_name.startswith('Wi-Fi'):
            for address in interface_addresses:
                if address.family == socket.AF_INET:
                    return address.address
    return None



def update_network_config(ip_address, network_config_path):
    try:
        tree = ET.parse(network_config_path)
        root = tree.getroot()
        ip_exists = False
        for string in root.findall('string'):
            if string.attrib['name'] == 'server_url':
                current_ip = string.text.split('//')[1].split(':')[0]
                if current_ip != ip_address:
                    string.text = f'http://{ip_address}:80/'
                    print("Current IP Address:", string.text)
                else:
                    print("Current IP Address:", string.text)
                    print("IP address already exists in the network config file.")
                    ip_exists = True
                break
        if not ip_exists:
            tree.write(network_config_path, encoding='utf-8', xml_declaration=True)
            print("Network config file updated successfully.")
    except Exception as e:
        print(f"Error updating network config file: {e}")

def update_network_security_config(ip_address, network_security_config_path):
    try:
        parser = ET.XMLParser(remove_blank_text=True)
        tree = ET.parse(network_security_config_path, parser)
        root = tree.getroot()
        domain_config = root.find('domain-config')
        
        # Check if the IP address already exists
        existing_domains = [domain.text for domain in domain_config.findall('domain')]
        if ip_address not in existing_domains:
            new_domain = ET.Element('domain', {'includeSubdomains': 'true'})
            new_domain.text = ip_address
            domain_config.append(ET.Comment("added through script"))
            domain_config.append(ET.Element('dummy'))  # Add a dummy element for formatting
            domain_config.insert(-1, new_domain)  # Insert the new domain before the dummy element
            domain_config.remove(domain_config[-1])  # Remove the dummy element
            tree.write(network_security_config_path, pretty_print=True, encoding='utf-8', xml_declaration=True)
            print("Network security config file updated successfully.")
        else:
            print("IP address already exists in the network security config file.")
    except Exception as e:
        print(f"Error updating network security config file: {e}")

wifi_ip = get_wifi_ip()
if wifi_ip:
    update_network_config(wifi_ip, network_config_path)
    update_network_security_config(wifi_ip, network_security_config_path)
else:
    print("Unable to retrieve WiFi IP address.")
