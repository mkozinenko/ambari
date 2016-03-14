var App = require("app");

App.ServiceUpdateVersionMapper = App.QuickDataMapper.create({
    model: App.ServiceUpdateVersion,

    config: {
        "id": "id",
        "version" : "version",
        "description" : "description",
        "updatable" : "updatable"
    },

    map: function(json){
        var result = [];

        json.role.forEach(function(item) {
            var parseResult = this.parseIt(item, this.get('config'));
            result.push(parseResult);
        }, this);

        App.store.loadMany(this.get('model'), result);
        App.store.commit();
    }
});
