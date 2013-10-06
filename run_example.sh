#!/bin/bash

echo "The order of the NV.visit calls reflect the Traversal "
java -cp lib/visitor.jar:lib/example.jar Main "$@"
