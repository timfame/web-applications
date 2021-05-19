## Usage

This app use Datomic Free with default settings. \
To start Datomic Free you can follow my instructions:
- [Download the latest version](https://my.datomic.com/downloads/free)
- `cd` to directory with extracted datomic free
- run `bin/transactor ./config/samples/free-transactor-template.properties`
this is a default properties, which will be enough to run application

You can run this app with: \
`java -jar testapp.jar` \
Maybe you need to specify Java Maximum Heap Size. It must be 512m at least: \
`java -Xmx512m -jar testapp.jar`

If you don't want to use Datomic, you can use in-memory storage, but you won't be able to use jar to run application. \
Go to `src-clj/core.clj`, to the bottom of the file, where you can find `-main` function. \
Replace `(d-store/get-store {})` with `(a-store/get-store)`, and now the app will use in-memory storage.

To run app manually, you need to run 2 commands:
 - `lein with-profile dev cljsbuild once` to build JavaScript from ClojureScript
 - `lein run` to start the server 

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
