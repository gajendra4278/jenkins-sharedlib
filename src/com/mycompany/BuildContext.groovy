package com.mycompany;


class BuildContext implements Serializable{
    def script

    def buildReport(){
        def result = [ 'channel':'@slackbot',
                       'color' : '',
                       'message': '' ]

        result
    }

}
