#!/usr/bin/env python

import json
import socket

from ambari_commons.exceptions import FatalException, NonFatalException
from ambari_commons.os_utils import run_in_shell, run_os_command, is_root, is_valid_filepath
from ambari_commons.logging_utils import set_verbose

_VERBOSE = False

def run_cmd(cmd, cwd=None):
    retcode, out, err = run_in_shell(cmd, cwd=cwd)
    if not retcode == 0:
        err = 'Error:' + out + " : " + err
        raise FatalException(1, err)

class PangeaSetup(object):

    def __init__(self):
        set_verbose(_VERBOSE)
        if not is_root():
            err = "Need root permissions"
            raise FatalException(1, err)

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
        command = [
            'sed -i.pangea.old -e \'1isearch node.consul cisco.com.node.consul node.dc1.consul cisco.com.node.dc1.consul\\nnameserver {}\\n\' -e \'d\' /etc/resolv.conf'.format(self.bind_addr)
            ]
        return command, None

    def setupHosts(self):
        command = [
            'sed -i.pangea.old -e \'/`hostname`/c{}\\t`hostname`\\t`hostname -s`\' /etc/hosts'.format(self.bind_addr)
            ]
        return command, None

    def setupCloudInit(self):
        if is_valid_filepath('/etc/cloud/cloud.cfg.d/10_etc_hosts.cfg'):
            command = [
                'sed -i.pangea.old -e \'/manage_etc_hosts/cmanage_etc_hosts: false\' /etc/cloud/cloud.cfg.d/10_etc_hosts.cfg'
                ]
        else:
            command = [
                'echo 0'
                ]
        return command, None

    def setupConsulRepo(self):
        command = [
            'curl -O http://128.107.33.156/repo/7/ambari/2.2.0/RPMS/ambari.repo'
            ]
        return command, '/etc/yum.repos.d'

    def setupConsul(self):
        command = [
            'yum install -y consul consul-ui'
            ]
        return command, None

    def setupConsulConf(self):
        fname = '/etc/consul/consul.json'
        with open(fname, 'w') as fp:
            fp.write(self.getConsulConf(self.bind_addr, is_bootstrap=True))
            fp.close()

        command = 'echo 0'
        return command, None

    def setupConsulDnsmasq(self):
        fname = '/etc/dnsmasq.d/10-consul'
        with open(fname, 'w') as fp:
            fp.write("server=/consul/127.0.0.1#8600\n" + \
                     "server=/consul/{}/127.0.0.1#8600\n".format(self.reverse_net_ptr(self.bind_addr)))
            fp.close()
        command = 'echo 0'
        return command, None

    def setupDnsmasqDefault(self):
        fname = '/etc/dnsmasq.d/00-default'
        with open(fname, 'w') as fp:
            fp.write("server=8.8.8.8\nserver=8.8.4.4\n")
            fp.close()
        command = 'echo 0'
        return command, None

    def setupDnsmasq(self):
        command = [
            'sed -i.pangea.old -e \'/^interface/d;/^listen_address/d;/^listen-address/d;$alisten-address={}\' /etc/dnsmasq.conf'.format(self.bind_addr)
            ]
        return command, None

    def setupNM(self):
        command = [
            'sed -i.pangea.old -e \'/\[main\]/adns=none\' /etc/NetworkManager/NetworkManager.conf'
            ]
        return command, None

    def setupNMiface(self):
        command = [
            'find /etc/sysconfig/network-scripts/ifcfg-eth? -exec sed -i.pangea.old \'s/^PEERDNS=.*$/PEERDNS=\"no\"/\' {} \;'
            ]
        return command, None

    def updateLibRequests(self):
        return ['easy_install -U requests'], None

    def restartConsul(self):
        return ['systemctl consul.service restart'], None

    def restartDnsmasq(self):
        return ['systemctl dnsmasq.service restart'], None

    def startConsul(self):
        return ['systemctl consul.service start'], None

    def stopConsul(self):
        return ['systemctl consul.service stop'], None

    def resetConsul(self):
        return [ 'rm -rf /var/lib/consul/*' ], None

    def deploy(self):
        cmds = [
            self.updateLibRequests(),
            self.setupResolv(),
            self.setupHosts(),
            self.setupCloudInit(),
            self.setupConsulRepo(),
            self.setupConsul(),
            self.setupConsulConf(),
            self.setupConsulDnsmasq(),
            self.setupDnsmasqDefault(),
            self.setupDnsmasq(),
            self.setupNM(),
            self.setupNMiface(),
            self.restartConsul(),
            self.restartDnsmasq()
        ]
        for cmd, cwd in cmds:
            run_cmd(cmd, cwd=cwd)

    def reset(self):
        cmds = [
            self.stopConsul(),
            self.resetConsul()
            self.startConsul()
        ]
        for cmd, cwd in cmds:
            run_cmd(cmd, cwd=cwd)

def pangea_setup(_options):
    p = PangeaSetup()
    p.deploy()

def pangea_reset(_options):
    p = PangeaSetup()
    p.reset()
