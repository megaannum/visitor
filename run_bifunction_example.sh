#!/bin/bash

echo "The order of the NV.visit calls reflect the Traversal "
MAIN=bifunction.Main
java -cp lib/visitor.jar:lib/example.jar $MAIN "$@"
