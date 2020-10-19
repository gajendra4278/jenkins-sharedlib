package com.mycompany;

import java.util.Map;

import hudson.tasks.test.AbstractTestResultAction;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.plugins.cobertura.Ratio;
import com.mycompany.BuildContext


class TestSummary implements Serializable{
    def script

    def rating = [ ""      : [ 'color': "",        "label": "Unknown"],
                   'FAILURE' : [ 'color': "danger",  "label": "Failed"],
                   'UNSTABLE': [ 'color': "warning", "label": "Unstable"],
                   'SUCCESS' : [ 'color': "good",    "label": "Success"]]

    String getTestSummary2() {
        def testStatus = "\n no tests published\n"

        AbstractTestResultAction testResultAction = script.currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
      
        if (testResultAction != null) {
            def total = testResultAction.totalCount
            def failed = testResultAction.failCount
            def skipped = testResultAction.skipCount
            def passed = total - failed - skipped
            testStatus = "Tests: \t*${passed}* _passed_ \t*${failed}${testResultAction.failureDiffString}* _failed_ \t*${skipped}* _skipped_"
        }
        return testStatus
    }
  
    String getCoverageSummary() {
      def coverageStatus = "no coverage published\n"
      def coverageAction = script.currentBuild.rawBuild.getAction(hudson.plugins.cobertura.CoberturaBuildAction.class)
      
      if (coverageAction != null ){
        def coverageResult = coverageAction.getResults()
        
        coverageStatus = "Coverage: "
        coverageStatus   += coverageResult[CoverageMetric.PACKAGES]    ? "*"+coverageResult[CoverageMetric.PACKAGES].toString()+"*"    + " _packages_ " : ""
        coverageStatus   += coverageResult[CoverageMetric.FILES]       ? "*"+coverageResult[CoverageMetric.FILES].toString()+"*"       + " _files_ " : ""
        coverageStatus   += coverageResult[CoverageMetric.CLASSES]     ? "*"+coverageResult[CoverageMetric.CLASSES].toString()+"*"     + " _classes_ " : ""
        coverageStatus   += coverageResult[CoverageMetric.METHOD]      ? "*"+coverageResult[CoverageMetric.METHOD].toString()+"*"      + " _methods_ " : ""
        coverageStatus   += coverageResult[CoverageMetric.LINE]        ? "*"+coverageResult[CoverageMetric.LINE].toString()+"*"        + " _lines_ " : ""
        coverageStatus   += coverageResult[CoverageMetric.CONDITIONAL] ? "*"+coverageResult[CoverageMetric.CONDITIONAL].toString()+"*" + " _conditionals_ " : ""
      }
      
      return coverageStatus
    }

    String getTitle(){
        def title = "*<${script.env.BUILD_URL}|${script.env.JOB_NAME} [${script.env.BUILD_NUMBER}]>*"
        title ? title :null
    }

    def getBuildCauseMessages(){
      def cause_messages = []
      def cause_message = ""
      for(cause in script.currentBuild.rawBuild.getCauses()){
        cause_messages.add(cause.getShortDescription())
      }
      cause_message = "Cause: " + cause_messages.join("\n")
      cause_message ? cause_message : null
    }

    def getChanges(){
        def changes = []
        def change_message = "No changes"
        def changeLogSets = script.currentBuild.changeSets
        
        for (int i = 0; i < changeLogSets.size(); i++) {
            //println(changeLogSets[i].getBrowser().getDescriptior().getDisplayName() )
            
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                changes.add("${entry.getPath()}")
                changes.add("\t${entry.getEditType().getName()}  ${entry.author}: ${entry.msg} / ${entry.affectedFiles.size()} affected files")
            }
        }
        if (changes){
            change_message = changes.join("\n")
        }
        "Changes: " + change_message
    }

    def getBuildInformation(includeChanges){
      def build_information = []
      def build_info = ""

      build_information.add(getTitle())
      build_information.add(getTestSummary2())
      build_information.add(getCoverageSummary())
      build_information.add(getBuildCauseMessages())
      if (includeChanges){
        build_information.add(getChanges())
      }

      build_info = build_information.join("\n")

      build_info ? build_info : null
    }

    def getColor(){

        def result = ""
        switch (script.currentBuild.result) {
            case "SUCCESS":
                result = "good"
                break
            case "UNSTABLE":
                result = "warning"
                break
            case "FAILURE":
                result = "danger"
                break
            default: result = ""
        }
        result
    }

    def getLabel(){
        def result = ""
        switch (script.currentBuild.result) {
            case "SUCCESS":
                result = "Success"
                break
            case "UNSTABLE":
                result = "Unstable"
                break
            case "FAILURE":
                result = "Failed"
                break
            default: result = "Unknown"
        }
        result
    }



}
