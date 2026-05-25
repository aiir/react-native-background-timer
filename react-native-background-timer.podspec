require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|

  s.name           = package['name']
  s.version        = package['version']
  s.summary        = package['description']
  s.homepage       = package['repository']['url']
  s.license        = package['license']
  s.author         = package['author']
  s.source         = { :git => 'https://github.com/aiir/react-native-background-timer.git', :tag => "v#{s.version}" }

  s.requires_arc   = true
  s.ios.deployment_target = '13.0'

  s.preserve_paths = 'README.md', 'package.json', 'index.js'
  s.source_files   = 'ios/*.{h,mm}'

  install_modules_dependencies(s)

end
