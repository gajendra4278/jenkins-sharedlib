#!groovy

if (env.BRANCH_NAME == 'master'){
    properties([
        pipelineTriggers([cron('@daily')]),
        buildDiscarder(logRotator(numToKeepStr: '10')),
    ])
}else{
    properties([
        buildDiscarder(logRotator(numToKeepStr: '10')),
    ])
}
