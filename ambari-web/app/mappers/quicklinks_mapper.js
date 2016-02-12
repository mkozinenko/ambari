var App = require("app");

App.quicklinksMapper = App.QuickDataMapper.create({
    model: App.QuickLinksConfig,

    config: {
        "id": "QuickLinkInfo.service_name",
        "file_name" : "QuickLinkInfo.file_name",
        "service_name" : "QuickLinkInfo.service_name",
        "stack_name" : "QuickLinkInfo.stack_name",
        "stack_version" : "QuickLinkInfo.stack_version",
        "name" : "QuickLinkInfo.quicklink_data.QuickLinksConfiguration.name",
        "protocol" : "QuickLinkInfo.quicklink_data.QuickLinksConfiguration.configuration.protocol",
        "links" : "QuickLinkInfo.quicklink_data.QuickLinksConfiguration.configuration.links"
    },

    map: function(json){
        console.time('App.quicklinksMapper execution time');

        var result = [];
        var linkResult = [];

        json.items.forEach(function(item) {
            var parseResult = this.parseIt(item, this.get('config'));
            console.log("parseResult", parseResult);
            result.push(parseResult);
        }, this);

        App.store.loadMany(this.get('model'), result);
        App.store.commit();
        console.timeEnd('App.quicklinksMapper execution time');
    }
});