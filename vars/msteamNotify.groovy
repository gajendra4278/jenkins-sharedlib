import com.mycompany.TestSummary


def notifyTeam(msteamhook, notifyBranches, notifyAllChangesOnSuccess, includeChanges){

  notifyBranches = notifyBranches ? notifyBranches: env.BRANCH_NAME
  if(msteamhook && (env.BRANCH_NAME in notifyBranches || !env.BRANCH_NAME )){
      summary = new TestSummary(script:this)
      build_information = summary.getBuildInformation(includeChanges)
      color = summary.getColor()
      label = summary.getLabel()

      echo color

       // Colors are good, warning, danger
      echo currentBuild.result
      if(currentBuild.result == 'SUCCESS'){
          def previousBuild = currentBuild.previousBuild
          echo currentBuild.previousBuild
          if(notifyAllChangesOnSuccess || (previousBuild != null && previousBuild.result != 'SUCCESS') ) {
             office365ConnectorSend message: "${label} - ${build_information}", webhookUrl: msteamhook, color: color
          }
          previuosBuild = null
      }else{
             office365ConnectorSend message: "${label} - ${build_information}", webhookUrl: msteamhook, color: color
      }
  }
}

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [msteamNotifyWebhook: "http://xvz",
                  msteamNotifyBranches: ["master"],
                  msteamNotifyAllChangesOnSuccess: false,
                  msteamNotifyIncludeChanges: false]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo currentBuild.result

    notifyTeam(config.msteamNotifyWebhook,
                config.msteamNotifyBranches,
                config.msteamNotifyAllChangesOnSuccess,
                config.msteamNotifyIncludeChanges)
    
  }

