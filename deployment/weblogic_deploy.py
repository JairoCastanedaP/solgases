import os


app_name = os.environ['WLS_APP_NAME']
war_path = os.environ['WLS_WAR_PATH']
target = os.environ.get('WLS_APP_TARGET', 'AdminServer')
user_config = os.environ['WLS_USER_CONFIG']
user_key = os.environ['WLS_USER_KEY']
admin_url = os.environ.get('WLS_ADMIN_URL', 't3://localhost:7001')
connected = False
deployment_error = False

try:
    connect(userConfigFile=user_config, userKeyFile=user_key, url=admin_url)
    connected = True

    if cmo.lookupAppDeployment(app_name) is None:
        print('Deploying application: ' + app_name)
        progress = deploy(app_name, war_path, targets=target, upload='true', block='true', timeout=600000)
    else:
        print('Redeploying application: ' + app_name)
        progress = redeploy(app_name, appPath=war_path, upload='true', block='true', timeout=600000)

    deployment_state = progress.getState()
    print('WebLogic deployment state: ' + str(deployment_state))
    if deployment_state != 'completed':
        raise Exception('WebLogic deployment did not complete: ' + str(deployment_state))
except:
    dumpStack()
    deployment_error = True
finally:
    if connected:
        disconnect()

if deployment_error:
    exit(exitcode=1)

exit(exitcode=0)
