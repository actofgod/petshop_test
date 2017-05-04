
Ext.ns('App', 'App.user');

App.user.CGrid = Ext.extend(Ext.grid.EditorGridPanel, {
    renderTo: 'user-grid',
    frame: true,
    title: 'Users',
    height: 620,
    width: 800,
    style: 'margin-top: 10px',

    initComponent : function() {

        // typical viewConfig
        this.viewConfig = {
            forceFit: true
        };

        // relay the Store's CRUD events into this grid so these events can be conveniently listened-to in our application-code.
        this.relayEvents(this.store, ['destroy', 'save', 'update']);

        this.bbar = this.buildBottomToolbar();

        // super
        App.user.CGrid.superclass.initComponent.call(this);
    },

    // paging bar on the bottom
    buildBottomToolbar: function () {
        return new Ext.PagingToolbar({
            pageSize: 20,
            store: userStore,
            displayInfo: true,
            displayMsg: 'Displaying users {0} - {1} of {2}',
            emptyMsg: "No users to display"
        });
    },

    /**
    * onDelete
    */
    onDelete : function(btn, ev) {
        var index = this.getSelectionModel().getSelectedCell();
        if (!index) {
            return false;
        }
        var rec = this.store.getAt(index[0]);
        this.store.remove(rec);
    }
});
