#!/usr/bin/env ruby
# frozen_string_literal: true
#
# One-off generator for ../PDA.xcodeproj (run again only if targets/schemes need to change -
# adding/removing source files needs nothing, since src/resources are folder references that
# reflect the filesystem live). Needs the `xcodeproj` gem: gem install --user-install xcodeproj
#
# MobiVM itself has no Xcode project of its own - :ios:launchIOSDevice etc. do everything from
# the command line. This wraps those same gradle tasks behind three PBXLegacyTargets ("external
# build tool" targets) so Xcode's own Product > Build (Cmd+B) can drive them with each one
# selected via the scheme picker, instead of a separate terminal. Cmd+R (Run/Debug) isn't wired:
# none of these targets produce a product Xcode itself knows how to launch/attach a debugger to
# (RoboVM's own AOT-compiled output isn't LLDB-debuggable via Xcode either), so Build is the
# actual trigger for all three, not Run.
require 'xcodeproj'

ios_dir = File.expand_path('..', __dir__)
project_path = File.join(ios_dir, 'PDA.xcodeproj')

project = Xcodeproj::Project.new(project_path)

# MARK: - Groups / file references (for browsing in Xcode's navigator)

ios_group = project.main_group.new_group('ios', '.')
%w[robovm.xml Info.plist.xml build.gradle.kts README.md].each do |f|
  ios_group.new_reference(f)
end

%w[src resources].each do |dir|
  ref = ios_group.new_reference(dir)
  ref.last_known_file_type = 'folder'
  ref.include_in_index = '0'
end

xcode_group = ios_group.new_group('xcode')
%w[build.sh run-on-device.sh run-in-simulator.sh generate_xcodeproj.rb].each do |f|
  xcode_group.new_reference("xcode/#{f}")
end

# MARK: - Targets: one PBXLegacyTarget per gradle entry point

def add_legacy_target(project, name, script)
  Xcodeproj::Project::ProjectHelper.new_legacy_target(
    project,
    name,
    "$(SRCROOT)/xcode/#{script}",
    '$(ACTION)',
    '$(SRCROOT)',
    '1'
  )
end

build_target = add_legacy_target(project, 'PDA (Build)', 'build.sh')
device_target = add_legacy_target(project, 'PDA (Run on Device)', 'run-on-device.sh')
simulator_target = add_legacy_target(project, 'PDA (Run in Simulator)', 'run-in-simulator.sh')

# MARK: - Shared schemes (checked into git, visible to anyone who opens the project)
#
# Not XCScheme#configure_with_targets: it computes a "buildable name" from the target's product
# file reference, which only PBXNativeTarget/PBXAggregateTarget have - PBXLegacyTarget has no
# product at all, so that raises "Unsupported build target type PBXLegacyTarget". Building the
# BuildableReference by hand and passing override_buildable_name=false skips that lookup.
def scheme_for_legacy_target(target)
  buildable_ref = Xcodeproj::XCScheme::BuildableReference.new(nil)
  buildable_ref.set_reference_target(target, false)

  entry = Xcodeproj::XCScheme::BuildAction::Entry.new(nil)
  entry.build_for_running = true
  entry.build_for_testing = true
  entry.build_for_profiling = true
  entry.build_for_archiving = true
  entry.build_for_analyzing = true
  entry.add_buildable_reference(buildable_ref)

  scheme = Xcodeproj::XCScheme.new
  scheme.build_action.add_entry(entry)
  scheme.build_action.parallelize_buildables = true
  scheme.build_action.build_implicit_dependencies = true
  scheme
end

[[build_target, 'PDA (Build)'], [device_target, 'PDA (Run on Device)'], [simulator_target, 'PDA (Run in Simulator)']].each do |target, scheme_name|
  scheme = scheme_for_legacy_target(target)
  scheme.save_as(project_path, scheme_name, true)
end

project.save

puts "Generated #{project_path}"
