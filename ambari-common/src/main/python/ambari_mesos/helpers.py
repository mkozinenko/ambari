import requests
import logging


logger = logging.getLogger("resource_management")


def get_mesos_leader(consul_url="http://localhost:8500"):
    """
    :consul_url: http://<host>:<port>, "http://localhost:8500" default value
    :return: hostname. unicode string. as example u'ambari-master-01.cisco.com'
    """

    consul_mesos = consul_url + "/v1/catalog/service/mesos"
    mesos_leader = ''
    try:
        r = requests.get(consul_mesos)
        for member in r.json():
            if 'leader' in member['ServiceTags']:
                mesos_leader = member.get('Node', None)
    except Exception:
        logger.error("Mesos leader was not found in consul")
        return None
    return mesos_leader


def get_marathon_leader_url(hosts, port="8080"):
    """

    :param hosts: list with marathon_hosts
    :return: marathon_leader url
    """
    local_tmpl = "http://{}:%s/v2/leader" % port
    for hst in hosts:
        try:
            r = requests.get(local_tmpl.format(hst))
            leader = r.json()['leader']
            logger.info("get_leader %s %s" % (leader, r.json()))
            if leader:
                return "http://{}".format(leader)
        except Exception as msg:
            logger.error("Error: %s " % msg)
            continue
    logger.error("marathon leader was not found")
    return None


def get_marathon_url(mesos_leader, port="5050"):
    """
    :param mesos_leader: hostname, or ip
    :return: as example u'http://ambari-master-01.cisco.com:8080'
    """
    marathon_ui_url = ''
    try:
        r = requests.get("http://" + mesos_leader + ":" + port + "/state.json")
        frameworks = r.json()['frameworks']
        for frm in frameworks:
            if frm['name'] == 'marathon':
                marathon_ui_url = frm.get('webui_url', None)
    except Exception:
        logger.error("Marathon webui_url was not found in mesos")
        return None
    return marathon_ui_url


def app_health_check_ok(marathon_url, app_id):
    """

    :param marathon_url:  http://<host>:<port> of marathon
    :param app_id:
    :return:
    """
    if marathon_url is None:
        raise Exception(
            "something went wrong, marathon_webui is %s" % marathon_url)

    try:
        url = "{marathon_url}/v2/apps/{id}".format(marathon_url=marathon_url, id=app_id)
        r = requests.get(url)
        data = r.json()
        healthy = True
        for task in data['app']['tasks']:
            for health_check in task.get('healthCheckResults', []):
                # explicit check so not to be confused. alive can be -
                # True or False
                if health_check.get('alive', False) == False:
                    healthy = False
        if healthy:
            return True
        logger.info("tasksUnhealthy: %s" % healthy)
    except Exception as msg:
        logger.error("Error while getting %s status: %s" % (app_id, msg))
    return False
