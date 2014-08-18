angular.module('modalInstanceControllers', [])

.controller('modalInstanceController',[
    '$scope',
    '$modalInstance',
    'object',
    'instanceId',
    function($scope, $modalInstance, object, instanceId) {
        $scope.object = object;

        // Set dialog
        if (instanceId != undefined) {
            // Update mode
            $scope.title = "Update Instance  " + instanceId + " of " + object.name;
            $scope.oklabel = "Update";
        } else {
            // Create mode
            $scope.title = "Create New Instance of " + object.name;
            $scope.oklabel = "Create";
        }

        // Create a working object
        var instance = {
            name : "Instance " + instanceId,
            id : instanceId,
            resources : []
        };
        for (j in object.resourcedefs) {
            var resourcedef = object.resourcedefs[j]
            instance.resources.push({
                def : resourcedef,
                id : resourcedef.id
            });
        }
        $scope.instance = instance

        
        // Define button function 
        $scope.submit = function() {
            $modalInstance.close($scope.instance);
        };
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);