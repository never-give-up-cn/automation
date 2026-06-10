package com.never_give_up.automation.demo.factory.dns;
//10. DNS 递归 / 迭代查询工厂（DnsRecursiveFactory）
public class DnsRecursiveFactory {
    public String resolve(String domain) {
        String root = askRoot(domain);
        String tld = askTld(domain, root);
        return askAuthoritative(domain, tld);
    }

    private String askRoot(String domain) { return "a.root-servers.net"; }
    private String askTld(String domain, String root) { return "ns.xxx.com"; }
    private String askAuthoritative(String domain, String tld) { return "1.2.3.4"; }
}