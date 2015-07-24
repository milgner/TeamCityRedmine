This is a plugin for TeamCity by Jetbrains to provide integration into the
popular Redmine issue tracker.

It uses the REST API to fetch information about issue tickets from a given
Redmine installation so it can be displayed in TeamCity.

To build it, first copy the provided build.properties.template to
build.properties and adjust the variables according to your system settings.
Depending on your setup, you may have to replace the provided scp deployment
with something more specific for your needs.
Otherwise you may just run `ant package` and manually copy the resulting
redmine.zip to the plugins directory of your TeamCity installation.

#### Builds on [public TeamCity server](https://teamcity.jetbrains.com/project.html?projectId=TeamCityThirdPartyPlugins_TeamCityRedmine&tab=projectOverview)

- [9.1.x version](https://teamcity.jetbrains.com/viewLog.html?buildTypeId=TeamCityThirdPartyPlugins_TeamCityRedmine_Master&buildId=lastPinned) [![](https://teamcity.jetbrains.com/app/rest/builds/buildType:TeamCityThirdPartyPlugins_TeamCityRedmine_Master,pinned:true/statusIcon)] (https://teamcity.jetbrains.com/app/rest/builds/buildType:TeamCityThirdPartyPlugins_TeamCityGithub_Master,pinned:true/statusIcon)