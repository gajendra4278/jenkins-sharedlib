import com.mycompany.TestSummary


def notifySlack(slackChannel, notifyBranches, notifyAllChangesOnSuccess, includeChanges){

  notifyBranches = notifyBranches ? notifyBranches: env.BRANCH_NAME
  if(slackChannel && (env.BRANCH_NAME in notifyBranches || !env.BRANCH_NAME )){
      summary = new TestSummary(script:this)
      build_information = summary.getBuildInformation(includeChanges)
      color = summary.getColor()
      label = summary.getLabel()

      echo color

       // Colors are good, warning, danger
      if(currentBuild.result == 'SUCCESS'){
          def previousBuild = currentBuild.previousBuild
          if(notifyAllChangesOnSuccess || (previousBuild != null && previousBuild.result != 'SUCCESS') ) {
             slackSend channel: slackChannel, color: color, message: "${label} - ${build_information}"
          }
          previuosBuild = null
      }else{
             slackSend channel: slackChannel, color: color, message: "${label} - ${build_information}"
      }
  }
}

def call(body) {
    // evaluate the body block, and collect configuration into the object
    echo "${body}"
    //echo body
    def config = [slackNotifyChannel: "@slackbot",
                  slackNotifyBranches: ["master"],
                  slackNotifyAllChangesOnSuccess: false,
                  slackNotifyIncludeChanges: false]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo currentBuild.result

    notifySlack(config.slackNotifyChannel,
                config.slackNotifyBranches,
                config.slackNotifyAllChangesOnSuccess,
                config.slackNotifyIncludeChanges)
  }

