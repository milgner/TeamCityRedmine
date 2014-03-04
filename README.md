This is a plugin for [TeamCity](www.jetbrains.com/teamcity/) to provide
integration into the popular [Redmine](http://www.redmine.org/) issue tracker.

It uses the REST API to fetch information about issue tickets from a given
Redmine installation so it can be displayed in TeamCity.

To build it, just invoke `mvn package` and it will download all required
dependencies and create a zip file which you can put into your TeamCity
plugins directory.
You can also quickly deploy it via SSH by copying and adapting the provided
`build.properties.template` (as `build.properties`) and use `ant deploy` to
copy it to your installation using SCP. *jsch support in Ant required.*
