#!/usr/bin/env python

import json
import socket
from ambari_commons.os_utils import run_os_command, is_root

class PangeaSetup(object):

    def __init__(self):
        if not is_root():
            print "Need root permissions"
            raise
        self.bind_addr = self.getLocalAddress()

    def getConsulConf(self, bind_addr='0.0.0.0', join_addr='', is_bootstrap=False):
        config = {
            'server': True,
            'bootstrap': is_bootstrap,
            'data_dir': '/var/lib/consul',
            'log_level': 'INFO',
            'bind_addr': bind_addr,
            'start_join': [
                join_addr
            ],
            'rejoin_after_leave': True,
            'ui_dir': '/usr/share/consul-ui',
            'ports': {
                'dns': 8600
            },
            'addresses': {
                'http': '0.0.0.0'
            }
        }
        return json.dumps(config, indent=4)

    def reverse_net_ptr(self, ip_addr):
        reverse_octets = ip_addr.split('.')[2::-1]
        return '.'.join(reverse_octets) + '.in-addr.arpa'

    def getLocalAddress(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('8.8.8.8', 53))
        return s.getsockname()[0]

    def setupResolv(self):
        command = "echo \"" + \
              "search node.consul cisco.com.node.consul node.dc1.consul cisco.com.node.dc1.consul\n" + \
              "nameserver {}\n".format(self.bind_addr) + \
              "\" > /etc/resolv.conf"
        return command

    def setupHosts(self):
        command = "sed -i -e \"/`hostname`/c{}\\t`hostname`\\t`hostname -s`\" /etc/hosts".format(self.bind_addr) + \
              " && " + \
              "[ -f /etc/cloud/cloud.cfg.d/10_etc_hosts.cfg ] && sudo sed -i -e \"/manage_etc_hosts/cmanage_etc_hosts: false\" /etc/cloud/cloud.cfg.d/10_etc_hosts.cfg || echo 0"
        return command

    def setupConsul(self):
        command = "pushd /etc/yum.repos.d &&" \
              "curl -O http://128.107.33.156/repo/7/ambari/2.2.0/RPMS/ambari.repo && " \
              "popd && " \
              "yum install -y consul consul-ui"
        return command

    def setupConsulConf(self):
        command = "echo '{}'".format(self.getConsulConf(self.bind_addr, is_bootstrap=True)) + \
            " > /etc/consul/consul.json && systemctl consul.service restart"
        return command

    def setupConsulDnsmasq(self):
        command = "echo \"" + \
              "server=/consul/127.0.0.1#8600\n" + \
              "server=/consul/{}/127.0.0.1#8600\n".format(self.reverse_net_ptr(self.bind_addr)) + \
              "\" > /etc/dnsmasq.d/10-consul"
        return command

    def setupDnsmasqDefault(self):
        command = "echo \"" + \
              "server=8.8.8.8\n" + \
              "server=8.8.4.4\n" + \
              "\" > /etc/dnsmasq.d/00-default"
        return command

    def setupDnsmasq(bind_addr):
        command = "sed \'/^interface/d;/^listen_address/d;/^listen-address/d;$alisten-address={}\' /etc/dnsmasq.conf".format(bind_addr) + \
              " && service dnsmasq restart && sleep 15"
        return command

    def setupNM(self):
        command = "echo \"dns=none\n\" >> /etc/NetworkManager.conf"
        return command

    def setupNMiface(self):
        command = "find /etc/sysconfig/network-scripts/ifcfg-eth? " + \
              "-exec sed -i.pangea 's/^PEERDNS=.*$/PEERDNS=\"no\"/' {} \;"
        return command

    def updateLibRequests(self):
        command = "sudo easy_install -U requests"
        return command

    def deploy(self):
        commands = [
            self.updateLibRequests(),
            self.setupResolv(),
            self.setupHosts(),
            self.setupConsul(),
            self.setupConsulConf(),
            self.setupConsulDnsmasq(),
            self.setupDnsmasqDefault(),
            self.setupDnsmasq(),
            self.setupNM(),
            self.setupNMiface(),
        ]
        for cmd in commands:
            run_os_command(cmd)

    def resetConsul(self):
        command = "systemctl stop consul.service && " + \
            "rm -rf /var/lib/consul/* && " + \
            "systemctl start consul.service && sleep 15 "
        return command

    def reset(self):
        commands = [
            self.resetConsul()
        ]
        for cmd in commands:
            run_os_command(cmd)

def pangea_setup(_options):
    p = PangeaSetup()
    p.deploy()

def pangea_reset(_options):
    p = PangeaSetup()
    p.reset()
